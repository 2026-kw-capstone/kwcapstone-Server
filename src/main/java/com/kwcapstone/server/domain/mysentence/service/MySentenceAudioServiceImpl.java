package com.kwcapstone.server.domain.mysentence.service;

import com.kwcapstone.server.domain.mysentence.client.MySentenceAiClient;
import com.kwcapstone.server.domain.mysentence.client.dto.request.MySentenceTtsAiReqDTO;
import com.kwcapstone.server.domain.mysentence.client.dto.request.PronunciationAnalyzeAiReqDTO;
import com.kwcapstone.server.domain.mysentence.client.dto.response.MySentenceTtsAiResDTO;
import com.kwcapstone.server.domain.mysentence.client.dto.response.PronunciationAnalyzeAiResDTO;
import com.kwcapstone.server.domain.mysentence.converter.MySentenceConverter;
import com.kwcapstone.server.domain.mysentence.dto.request.MySentenceAnalyzeReqDTO;
import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceAnalyzeResDTO;
import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceUserAudioResDTO;
import com.kwcapstone.server.domain.mysentence.entity.MySentence;
import com.kwcapstone.server.domain.mysentence.entity.MySentenceAnalysisResult;
import com.kwcapstone.server.domain.mysentence.exception.code.MySentenceErrorCode;
import com.kwcapstone.server.domain.mysentence.repository.MySentenceAnalysisResultRepository;
import com.kwcapstone.server.domain.mysentence.repository.MySentenceRepository;
import com.kwcapstone.server.global.apiPayload.exception.CustomException;
import com.kwcapstone.server.global.security.SecurityUtil;
import com.kwcapstone.server.global.storage.audio.AudioStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MySentenceAudioServiceImpl implements MySentenceAudioService {

    private static final String MEMBER_AUDIO_PREFIX = "warmups/my-sentence/member";
    private static final String MODEL_AUDIO_PREFIX = "warmups/my-sentence/model";

    private static final String MODEL_AUDIO_EXTENSION = ".mp3";
    private static final String MODEL_AUDIO_CONTENT_TYPE = "audio/mpeg";

    private static final String TTS_VOICE = "nova";
    private static final double TTS_SPEED = 1.0;
    private static final long PRESIGNED_EXPIRES_IN = 600L;

    private final MySentenceRepository mySentenceRepository;
    private final MySentenceAiClient mySentenceAiClient;
    private final AudioStorageService audioStorageService;
    private final MySentenceAnalysisResultRepository mySentenceAnalysisResultRepository;
    private final ObjectMapper objectMapper;

    @Override
    public MySentenceTtsAiResDTO getTts(Long sentenceId) {
        Long memberId = SecurityUtil.getCurrentMemberId();

        MySentence mySentence = mySentenceRepository.findByIdAndDeletedAtIsNull(sentenceId)
                .orElseThrow(() -> new CustomException(MySentenceErrorCode.MY_SENTENCE_NOT_FOUND));

        if (!mySentence.getMember().getId().equals(memberId)) {
            throw new CustomException(MySentenceErrorCode.MY_SENTENCE_FORBIDDEN);
        }

        String modelVoiceUrl = generateModelVoiceUrl(mySentence);

        return new MySentenceTtsAiResDTO(
                mySentence.getId(),
                mySentence.getSentenceContent(),
                modelVoiceUrl,
                PRESIGNED_EXPIRES_IN
        );
    }

    @Override
    @Transactional(readOnly = true)
    public MySentenceUserAudioResDTO getUserAudio(Long sentenceId) {
        Long memberId = SecurityUtil.getCurrentMemberId();

        MySentence mySentence = mySentenceRepository.findByIdAndDeletedAtIsNull(sentenceId)
                .orElseThrow(() -> new CustomException(MySentenceErrorCode.MY_SENTENCE_NOT_FOUND));

        if (!mySentence.getMember().getId().equals(memberId)) {
            throw new CustomException(MySentenceErrorCode.MY_SENTENCE_FORBIDDEN);
        }

        MySentenceAnalysisResult latestResult =
                mySentenceAnalysisResultRepository
                        .findTopByMySentenceIdAndMemberIdOrderByCreatedAtDesc(sentenceId, memberId)
                        .orElseThrow(() -> new CustomException(
                                MySentenceErrorCode.RECENT_USER_AUDIO_NOT_FOUND
                        ));

        if (!StringUtils.hasText(latestResult.getUserAudioKey())) {
            throw new CustomException(MySentenceErrorCode.RECENT_USER_AUDIO_NOT_FOUND);
        }

        String userAudioUrl =
                audioStorageService.generatePresignedGetUrl(latestResult.getUserAudioKey());

        return new MySentenceUserAudioResDTO(
                mySentence.getId(),
                latestResult.getId(),
                userAudioUrl,
                PRESIGNED_EXPIRES_IN
        );
    }

    @Override
    public MySentenceAnalyzeResDTO analyzePronunciation(MySentenceAnalyzeReqDTO request) {
        Long memberId = SecurityUtil.getCurrentMemberId();

        // 중복 요청 확인
        MySentenceAnalysisResult duplicated =
                mySentenceAnalysisResultRepository
                        .findByMemberIdAndClientRequestId(
                                memberId,
                                request.getClientRequestId()
                        )
                        .orElse(null);

        if (duplicated != null) {
            return toAnalyzeResponseWithSavedWordAnalysis(duplicated);
        }

        MySentence mySentence =
                mySentenceRepository.findByIdAndDeletedAtIsNull(request.getSentenceId())
                        .orElseThrow(() -> new CustomException(
                                MySentenceErrorCode.MY_SENTENCE_NOT_FOUND
                        ));

        if (!mySentence.getMember().getId().equals(memberId)) {
            throw new CustomException(MySentenceErrorCode.MY_SENTENCE_FORBIDDEN);
        }

        // 사용자 녹음 파일 S3 업로드
        String memberAudioKey = audioStorageService.upload(
                buildMemberAudioKeyPrefix(memberId),
                request.getClientRequestId(),
                request.getVoiceFile()
        );

        try {
            String memberVoiceUrlForAi =
                    audioStorageService.generatePresignedGetUrl(memberAudioKey);

            PronunciationAnalyzeAiResDTO aiResult =
                    mySentenceAiClient.analyzePronunciation(
                            new PronunciationAnalyzeAiReqDTO(
                                    memberVoiceUrlForAi,
                                    mySentence.getSentenceContent()
                            )
                    );

            validateAiResult(aiResult);

            List<MySentenceAnalyzeResDTO.WordAnalysis> wordAnalysis =
                    MySentenceConverter.toWordAnalysisResponse(aiResult);

            String wordAnalysisJson =
                    objectMapper.writeValueAsString(wordAnalysis);

            MySentenceAnalysisResult analysisResult =
                    MySentenceAnalysisResult.builder()
                            .mySentence(mySentence)
                            .member(mySentence.getMember())
                            .clientRequestId(request.getClientRequestId())
                            .userAudioKey(memberAudioKey)
                            .pronunciationScore(aiResult.getPronunciationScore())
                            .speechRateScore(aiResult.getVoiceAnalysis()
                                    .getSpeechRate()
                                    .getScore())
                            .silenceRatio(aiResult.getVoiceAnalysis()
                                    .getSilenceRatio()
                                    .getPausePercent())
                            .aiFeedback(aiResult.getFeedback())
                            .syllableResultJson(wordAnalysisJson)
                            .build();

            MySentenceAnalysisResult savedResult =
                    mySentenceAnalysisResultRepository.save(analysisResult);

            return MySentenceConverter.toAnalyzeResponse(savedResult, wordAnalysis);

        } catch (Exception e) {
            audioStorageService.delete(memberAudioKey);

            if (e instanceof CustomException customException) {
                throw customException;
            }

            throw new CustomException(MySentenceErrorCode.PRONUNCIATION_ANALYSIS_FAILED);
        }
    }

    // 모범 음성 URL 생성 메서드
    private String generateModelVoiceUrl(MySentence mySentence) {
        String modelAudioKey = audioStorageService.buildKey(
                MODEL_AUDIO_PREFIX,
                String.valueOf(mySentence.getId()),
                MODEL_AUDIO_EXTENSION
        );

        // S3에 해당 모범 음성 파일이 없는 경우 AI 서버의 TTS를 통해 생성
        if (!audioStorageService.exists(modelAudioKey)) {
            byte[] audioBytes = mySentenceAiClient.requestTts(
                    new MySentenceTtsAiReqDTO(
                            mySentence.getSentenceContent(),
                            TTS_VOICE,
                            TTS_SPEED
                    )
            );

            audioStorageService.uploadBytes(
                    modelAudioKey,
                    audioBytes,
                    MODEL_AUDIO_CONTENT_TYPE
            );
        }
        return audioStorageService.generatePresignedGetUrl(modelAudioKey);
    }

    // 사용자 음성 저장 경로 prefix 생성 메서드
    private String buildMemberAudioKeyPrefix(Long memberId) {
        return MEMBER_AUDIO_PREFIX + "/" + memberId;
    }

    // 중복 요청 시 저장된 wordAnalysis JSON으로 응답 생성
    private MySentenceAnalyzeResDTO toAnalyzeResponseWithSavedWordAnalysis(
            MySentenceAnalysisResult analysisResult
    ) {
        try {
            List<MySentenceAnalyzeResDTO.WordAnalysis> wordAnalysis =
                    objectMapper.readValue(
                            analysisResult.getSyllableResultJson(),
                            new TypeReference<List<MySentenceAnalyzeResDTO.WordAnalysis>>() {
                            }
                    );

            return MySentenceConverter.toAnalyzeResponse(
                    analysisResult,
                    wordAnalysis
            );
        } catch (Exception e) {
            throw new CustomException(MySentenceErrorCode.PRONUNCIATION_ANALYSIS_FAILED);
        }
    }

    private void validateAiResult(PronunciationAnalyzeAiResDTO aiResult) {
        if (aiResult == null || !Boolean.TRUE.equals(aiResult.getSuccess())) {
            throw new CustomException(MySentenceErrorCode.PRONUNCIATION_ANALYSIS_FAILED);
        }
    }
}