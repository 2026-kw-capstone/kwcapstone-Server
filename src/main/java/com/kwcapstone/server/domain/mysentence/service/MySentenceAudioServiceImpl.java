package com.kwcapstone.server.domain.mysentence.service;

import com.kwcapstone.server.domain.mysentence.client.MySentenceAiClient;
import com.kwcapstone.server.domain.mysentence.dto.request.MySentenceTtsReqDTO;
import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceTtsResDTO;
import com.kwcapstone.server.domain.mysentence.entity.MySentence;
import com.kwcapstone.server.domain.mysentence.exception.code.MySentenceErrorCode;
import com.kwcapstone.server.domain.mysentence.repository.MySentenceRepository;
import com.kwcapstone.server.global.apiPayload.exception.CustomException;
import com.kwcapstone.server.global.security.SecurityUtil;
import com.kwcapstone.server.global.storage.audio.AudioStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MySentenceAudioServiceImpl implements MySentenceAudioService {

    private static final String PREFIX_ROOT = "my-sentence";
    private static final String TTS_VOICE = "nova";
    private static final double TTS_SPEED = 1.0;
    private static final long PRESIGNED_EXPIRES_IN = 600L;

    private final MySentenceRepository mySentenceRepository;
    private final MySentenceAiClient mySentenceAiClient;
    private final AudioStorageService audioStorageService;

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
}