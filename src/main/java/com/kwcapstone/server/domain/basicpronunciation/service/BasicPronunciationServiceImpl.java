package com.kwcapstone.server.domain.basicpronunciation.service;

import com.kwcapstone.server.domain.basicpronunciation.client.BasicPronunciationAiClient;
import com.kwcapstone.server.domain.basicpronunciation.client.dto.request.VowelPracticeAiReqDTO;
import com.kwcapstone.server.domain.basicpronunciation.client.dto.response.VowelPracticeAiResDTO;
import com.kwcapstone.server.domain.basicpronunciation.converter.BasicPronunciationConverter;
import com.kwcapstone.server.domain.basicpronunciation.dto.request.BasicPronunciationPracticeReqDTO;
import com.kwcapstone.server.domain.basicpronunciation.dto.response.BasicPronunciationLatestResDTO;
import com.kwcapstone.server.domain.basicpronunciation.dto.response.BasicPronunciationPracticeResDTO;
import com.kwcapstone.server.domain.basicpronunciation.entity.BasicPronunciationPractice;
import com.kwcapstone.server.domain.basicpronunciation.enums.BasicVowel;
import com.kwcapstone.server.domain.basicpronunciation.exception.code.BasicPronunciationErrorCode;
import com.kwcapstone.server.domain.basicpronunciation.repository.BasicPronunciationPracticeRepository;
import com.kwcapstone.server.domain.member.entity.Member;
import com.kwcapstone.server.domain.member.exception.code.MemberErrorCode;
import com.kwcapstone.server.domain.member.repository.MemberRepository;
import com.kwcapstone.server.global.apiPayload.exception.CustomException;
import com.kwcapstone.server.global.apiPayload.response.ErrorCode;
import com.kwcapstone.server.global.security.SecurityUtil;
import com.kwcapstone.server.global.storage.audio.AudioStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BasicPronunciationServiceImpl implements BasicPronunciationService {
    private static final String MEMBER_AUDIO_PREFIX = "warmups/basic-pronunciation/member";
    private static final String MODEL_AUDIO_PREFIX = "warmups/basic-pronunciation/model";
    private static final String MODEL_AUDIO_EXTENSION = ".mp3";
    private static final String MODEL_AUDIO_CONTENT_TYPE = "audio/mpeg";

    private final BasicPronunciationPracticeRepository basicPronunciationPracticeRepository;
    private final MemberRepository memberRepository;
    private final AudioStorageService audioStorageService;
    private final BasicPronunciationAiClient basicPronunciationAiClient;

    @Override
    public BasicPronunciationPracticeResDTO analyzePractice(BasicPronunciationPracticeReqDTO request) {
        Long memberId = SecurityUtil.getCurrentMemberId();

        // 중복 요청 확인
        BasicPronunciationPractice duplicated = basicPronunciationPracticeRepository
                .findByMemberIdAndClientRequestId(memberId, request.getClientRequestId())
                .orElse(null);

        if (duplicated != null) {
            return toPracticeResponseWithUrls(duplicated);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 사용자 녹음 파일 S3 업로드
        String memberAudioKey = audioStorageService.upload(
                buildMemberAudioKeyPrefix(memberId),
                request.getClientRequestId(),
                request.getVoiceFile()
        );

        try {
            String memberVoiceUrlForAi = audioStorageService.generatePresignedGetUrl(memberAudioKey);

            VowelPracticeAiResDTO aiResult = basicPronunciationAiClient.analyzeVowel(
                    new VowelPracticeAiReqDTO(memberVoiceUrlForAi, request.getTargetVowel().getKorean())
            );

            BigDecimal accuracyScore = resolveAccuracyScore(aiResult);
            String feedback = resolveFeedback(aiResult);

            BasicPronunciationPractice practice = BasicPronunciationConverter.toPractice(
                    member,
                    request.getClientRequestId(),
                    request.getTargetVowel(),
                    memberAudioKey,
                    accuracyScore,
                    feedback
            );

            BasicPronunciationPractice savedPractice = basicPronunciationPracticeRepository.save(practice);

            return toPracticeResponseWithUrls(savedPractice);
        } catch (Exception e) {
            audioStorageService.delete(memberAudioKey);

            throw e;
        }
    }

    @Override
    public BasicPronunciationLatestResDTO getLatestPractice(BasicVowel targetVowel) {
        Long memberId = SecurityUtil.getCurrentMemberId();

        return basicPronunciationPracticeRepository
                .findTopByMemberIdAndTargetVowelOrderByCreatedAtDesc(memberId, targetVowel)
                .map(this::toLatestResponseWithUrls)
                .orElseGet(BasicPronunciationConverter::toLatestEmptyResponse);
    }

    // 응답 생성 메서드
    private BasicPronunciationPracticeResDTO toPracticeResponseWithUrls(BasicPronunciationPractice practice) {
        String voiceUrl = audioStorageService.generatePresignedGetUrl(practice.getMemberAudioKey());
        String modelVoiceUrl = generateModelVoiceUrl(practice.getTargetVowel());

        return BasicPronunciationConverter.toPracticeResponse(
                practice,
                voiceUrl,
                modelVoiceUrl
        );
    }

    private BasicPronunciationLatestResDTO toLatestResponseWithUrls(BasicPronunciationPractice practice) {
        String voiceUrl = audioStorageService.generatePresignedGetUrl(practice.getMemberAudioKey());
        String modelVoiceUrl = generateModelVoiceUrl(practice.getTargetVowel());

        return BasicPronunciationConverter.toLatestResponse(
                practice,
                voiceUrl,
                modelVoiceUrl
        );
    }

    // 모범 음성 URL 생성 메서드
    private String generateModelVoiceUrl(BasicVowel targetVowel) {
        String modelAudioKey = audioStorageService.buildKey(
                MODEL_AUDIO_PREFIX,
                targetVowel.name(),
                MODEL_AUDIO_EXTENSION
        );

        // S3에 해당 모범 음성 파일이 없는 경우 AI 서버의 TTS를 통해 생성
        if (!audioStorageService.exists(modelAudioKey)) {
            byte[] audioBytes = basicPronunciationAiClient.synthesizeSpeech(targetVowel.getKorean());
            audioStorageService.uploadBytes(modelAudioKey, audioBytes, MODEL_AUDIO_CONTENT_TYPE);
        }

        return audioStorageService.generatePresignedGetUrl(modelAudioKey);
    }

    // 사용자 음성 저장 경로 prefix 생성 메서드
    private String buildMemberAudioKeyPrefix(Long memberId) {
        return MEMBER_AUDIO_PREFIX + "/" + memberId;
    }

    private BigDecimal resolveAccuracyScore(VowelPracticeAiResDTO aiResult) {
        if (aiResult != null && aiResult.getPronunciationScore() != null) {
            return aiResult.getPronunciationScore();
        }

        throw new CustomException(ErrorCode.AI_SERVER_ERROR);
    }

    private String resolveFeedback(VowelPracticeAiResDTO aiResult) {
        if (aiResult != null && StringUtils.hasText(aiResult.getFeedback())) {
            return aiResult.getFeedback().trim();
        }

        throw new CustomException(BasicPronunciationErrorCode.EMPTY_FEEDBACK);
    }
}
