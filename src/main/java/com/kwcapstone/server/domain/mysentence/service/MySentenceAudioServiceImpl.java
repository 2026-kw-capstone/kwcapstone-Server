package com.kwcapstone.server.domain.mysentence.service;

import com.kwcapstone.server.domain.mysentence.client.MySentenceAiClient;
import com.kwcapstone.server.domain.mysentence.converter.MySentenceConverter;
import com.kwcapstone.server.domain.mysentence.dto.request.MySentenceTtsReqDTO;
import com.kwcapstone.server.domain.mysentence.dto.request.PronunciationAnalyzeAiReqDTO;
import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceAnalyzeResDTO;
import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceTtsResDTO;
import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceUserAudioResDTO;
import com.kwcapstone.server.domain.mysentence.dto.response.PronunciationAnalyzeAiResDTO;
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
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Transactional
public class MySentenceAudioServiceImpl implements MySentenceAudioService {

    private static final String PREFIX_ROOT = "my-sentence";
    private static final String TTS_VOICE = "nova";
    private static final double TTS_SPEED = 1.0;
    private static final long PRESIGNED_EXPIRES_IN = 600L;
    private static final long MAX_AUDIO_SIZE = 10 * 1024 * 1024;

    private final MySentenceRepository mySentenceRepository;
    private final MySentenceAiClient mySentenceAiClient;
    private final AudioStorageService audioStorageService;
    private final MySentenceAnalysisResultRepository mySentenceAnalysisResultRepository;

    private final ObjectMapper objectMapper;

    @Override
    public MySentenceTtsResDTO getTts(Long sentenceId) {
        Long memberId = SecurityUtil.getCurrentMemberId();

        MySentence mySentence = mySentenceRepository.findByIdAndDeletedAtIsNull(sentenceId)
                .orElseThrow(() -> new CustomException(MySentenceErrorCode.MY_SENTENCE_NOT_FOUND));

        if (!mySentence.getMember().getId().equals(memberId)) {
            throw new CustomException(MySentenceErrorCode.MY_SENTENCE_FORBIDDEN);
        }

        if (mySentence.getAiAudioKey() == null || mySentence.getAiAudioKey().isBlank()) {
            byte[] mp3Bytes = mySentenceAiClient.requestTts(
                    new MySentenceTtsReqDTO(
                            mySentence.getSentenceContent(),
                            TTS_VOICE,
                            TTS_SPEED
                    )
            );

            String keyPrefix = PREFIX_ROOT + "/" + memberId;
            String fileBaseName = "sentence-" + sentenceId + "-tts";

            String aiAudioKey = audioStorageService.uploadBytes(
                    keyPrefix,
                    fileBaseName,
                    mp3Bytes,
                    "audio/mpeg",
                    "mp3"
            );

            mySentence.updateAiAudioKey(aiAudioKey);
        }

        String aiAudioUrl = audioStorageService.generatePresignedGetUrl(mySentence.getAiAudioKey());

        return new MySentenceTtsResDTO(
                mySentence.getId(),
                mySentence.getSentenceContent(),
                aiAudioUrl,
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
                        .findTopByMySentence_IdAndMember_IdOrderByCreatedAtDesc(sentenceId, memberId)
                        .orElseThrow(() -> new CustomException(
                                MySentenceErrorCode.RECENT_USER_AUDIO_NOT_FOUND
                        ));

        if (latestResult.getUserAudioKey() == null || latestResult.getUserAudioKey().isBlank()) {
            throw new CustomException(MySentenceErrorCode.RECENT_USER_AUDIO_NOT_FOUND);
        }

        String userAudioUrl = audioStorageService.generatePresignedGetUrl(latestResult.getUserAudioKey());

        return new MySentenceUserAudioResDTO(
                mySentence.getId(),
                latestResult.getId(),
                userAudioUrl,
                600L
        );
    }

    @Override
    @Transactional
    public MySentenceAnalyzeResDTO analyzePronunciation(
            Long sentenceId,
            MultipartFile voiceFile
    ) {
        Long memberId = SecurityUtil.getCurrentMemberId();

        validateVoiceFile(voiceFile);

        MySentence mySentence = mySentenceRepository.findByIdAndDeletedAtIsNull(sentenceId)
                .orElseThrow(() -> new CustomException(MySentenceErrorCode.MY_SENTENCE_NOT_FOUND));

        if (!mySentence.getMember().getId().equals(memberId)) {
            throw new CustomException(MySentenceErrorCode.MY_SENTENCE_FORBIDDEN);
        }

        String keyPrefix = "my-sentence/" + memberId;
        String fileBaseName = "sentence-" + sentenceId + "-user-" + System.currentTimeMillis();

        String userAudioKey = audioStorageService.upload(
                keyPrefix,
                fileBaseName,
                voiceFile
        );

        String userAudioPresignedUrl =
                audioStorageService.generatePresignedGetUrl(userAudioKey);

        PronunciationAnalyzeAiResDTO aiResponse =
                mySentenceAiClient.analyzePronunciation(
                        new PronunciationAnalyzeAiReqDTO(
                                userAudioPresignedUrl,
                                mySentence.getSentenceContent()
                        )
                );

        if (aiResponse == null || !Boolean.TRUE.equals(aiResponse.getSuccess())) {
            throw new CustomException(MySentenceErrorCode.PRONUNCIATION_ANALYSIS_FAILED);
        }

        try {
            String wordAnalysisJson = objectMapper.writeValueAsString(
                    aiResponse.getWordAnalysis()
            );

            MySentenceAnalysisResult analysisResult =
                    MySentenceAnalysisResult.builder()
                            .mySentence(mySentence)
                            .member(mySentence.getMember())
                            .userAudioKey(userAudioKey)
                            .pronunciationScore(aiResponse.getPronunciationScore())
                            .speechRateScore(aiResponse.getVoiceAnalysis()
                                    .getSpeechRate()
                                    .getScore())
                            .silenceRatio(aiResponse.getVoiceAnalysis()
                                    .getSilenceRatio()
                                    .getPausePercent())
                            .aiFeedback(aiResponse.getFeedback())
                            .syllableResultJson(wordAnalysisJson)
                            .build();

            MySentenceAnalysisResult savedResult =
                    mySentenceAnalysisResultRepository.save(analysisResult);

            return MySentenceConverter.toAnalyzeResponse(savedResult, aiResponse);

        } catch (Exception e) {
            throw new CustomException(MySentenceErrorCode.PRONUNCIATION_ANALYSIS_FAILED);
        }
    }

    // 파일 검증 매서드
    private void validateVoiceFile(MultipartFile voiceFile) {
        if (voiceFile == null || voiceFile.isEmpty()) {
            throw new CustomException(MySentenceErrorCode.AUDIO_FILE_REQUIRED);
        }

        if (voiceFile.getSize() > MAX_AUDIO_SIZE) {
            throw new CustomException(MySentenceErrorCode.UNSUPPORTED_AUDIO_FORMAT);
        }

        String contentType = voiceFile.getContentType();

        if (contentType == null ||
                !(contentType.equals("audio/mpeg")
                        || contentType.equals("audio/mp3")
                        || contentType.equals("audio/wav")
                        || contentType.equals("audio/webm")
                        || contentType.equals("audio/x-m4a")
                        || contentType.equals("audio/mp4"))) {
            throw new CustomException(MySentenceErrorCode.UNSUPPORTED_AUDIO_FORMAT);
        }
    }
}