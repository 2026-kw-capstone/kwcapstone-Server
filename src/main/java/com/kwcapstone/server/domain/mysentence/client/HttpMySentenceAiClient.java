package com.kwcapstone.server.domain.mysentence.client;

import com.kwcapstone.server.domain.mysentence.dto.request.MySentenceTtsReqDTO;
import com.kwcapstone.server.domain.mysentence.dto.request.PronunciationAnalyzeAiReqDTO;
import com.kwcapstone.server.domain.mysentence.dto.response.PronunciationAnalyzeAiResDTO;
import com.kwcapstone.server.domain.mysentence.exception.code.MySentenceErrorCode;
import com.kwcapstone.server.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class HttpMySentenceAiClient implements MySentenceAiClient {

    private final RestClient aiRestClient;

    @Override
    public byte[] requestTts(MySentenceTtsReqDTO request) {
        try {
            return aiRestClient.post()
                    .uri("/tts")
                    .body(request)
                    .retrieve()
                    .body(byte[].class);
        } catch (Exception e) {
            throw new CustomException(MySentenceErrorCode.TTS_GENERATION_FAILED);
        }
    }

    @Override
    public PronunciationAnalyzeAiResDTO analyzePronunciation(
            PronunciationAnalyzeAiReqDTO request
    ) {
        try {
            return aiRestClient.post()
                    .uri("/practice/reference")
                    .body(request)
                    .retrieve()
                    .body(PronunciationAnalyzeAiResDTO.class);
        } catch (Exception e) {
            throw new CustomException(MySentenceErrorCode.PRONUNCIATION_ANALYSIS_FAILED);
        }
    }
}