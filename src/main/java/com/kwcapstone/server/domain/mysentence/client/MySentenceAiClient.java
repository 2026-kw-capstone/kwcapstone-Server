package com.kwcapstone.server.domain.mysentence.client;

import com.kwcapstone.server.domain.mysentence.client.dto.request.MySentenceTtsAiReqDTO;
import com.kwcapstone.server.domain.mysentence.client.dto.request.PronunciationAnalyzeAiReqDTO;
import com.kwcapstone.server.domain.mysentence.client.dto.response.PronunciationAnalyzeAiResDTO;

public interface MySentenceAiClient {
    byte[] requestTts(MySentenceTtsAiReqDTO request);  // TTS
    PronunciationAnalyzeAiResDTO analyzePronunciation(PronunciationAnalyzeAiReqDTO request);  // 발음 분석
}
