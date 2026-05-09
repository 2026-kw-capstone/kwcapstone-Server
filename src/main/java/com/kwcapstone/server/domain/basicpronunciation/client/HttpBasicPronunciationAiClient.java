package com.kwcapstone.server.domain.basicpronunciation.client;

import com.kwcapstone.server.domain.basicpronunciation.client.dto.request.TtsAiReqDTO;
import com.kwcapstone.server.domain.basicpronunciation.client.dto.request.VowelPracticeAiReqDTO;
import com.kwcapstone.server.domain.basicpronunciation.client.dto.response.VowelPracticeAiResDTO;
import com.kwcapstone.server.domain.basicpronunciation.exception.code.BasicPronunciationErrorCode;
import com.kwcapstone.server.global.apiPayload.exception.CustomException;
import com.kwcapstone.server.global.apiPayload.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
@RequiredArgsConstructor
public class HttpBasicPronunciationAiClient implements BasicPronunciationAiClient {
    private static final String TTS_VOICE = "nova";
    private static final double TTS_SPEED = 1.0;

    private final RestClient aiRestClient;

    @Override
    public VowelPracticeAiResDTO analyzeVowel(VowelPracticeAiReqDTO request) {
        try {
            VowelPracticeAiResDTO response = aiRestClient.post()
                    .uri("/practice/vowel")
                    .body(request)
                    .retrieve()
                    .body(VowelPracticeAiResDTO.class);

            if (response == null) {
                log.error("AI server returned null response. uri=/practice/vowel");

                throw new CustomException(ErrorCode.AI_SERVER_ERROR);
            }

            if (Boolean.FALSE.equals(response.getSuccess())) {
                log.error(
                        "AI server returned failure response. uri=/practice/vowel, success={}",
                        response.getSuccess()
                );

                throw new CustomException(ErrorCode.AI_SERVER_ERROR);
            }

            return response;
        } catch (RestClientException e) {
            log.error(
                    "Failed to call AI server. uri=/practice/vowel, message={}",
                    e.getMessage(),
                    e
            );

            throw new CustomException(ErrorCode.AI_SERVER_ERROR);
        }
    }

    @Override
    public byte[] synthesizeSpeech(String text) {
        try {
            byte[] response = aiRestClient.post()
                    .uri("/tts")
                    .body(new TtsAiReqDTO(text, TTS_VOICE, TTS_SPEED))
                    .retrieve()
                    .body(byte[].class);

            if (response == null || response.length == 0) {
                log.error("AI server returned empty TTS audio. uri=/tts, text={}", text);

                throw new CustomException(BasicPronunciationErrorCode.EMPTY_TTS_AUDIO);
            }

            return response;
        } catch (RestClientException e) {
            log.error(
                    "Failed to call AI server. uri=/tts, text={}, message={}",
                    text,
                    e.getMessage(),
                    e
            );

            throw new CustomException(ErrorCode.AI_SERVER_ERROR);
        }
    }
}
