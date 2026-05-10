package com.kwcapstone.server.domain.mysentence.client;

import com.kwcapstone.server.domain.mysentence.dto.request.MySentenceTtsReqDTO;
import com.kwcapstone.server.domain.mysentence.dto.request.PronunciationAnalyzeAiReqDTO;
import com.kwcapstone.server.domain.mysentence.dto.response.PronunciationAnalyzeAiResDTO;

public interface MySentenceAiClient {
    byte[] requestTts(MySentenceTtsReqDTO request);  // TTS
    PronunciationAnalyzeAiResDTO analyzePronunciation(PronunciationAnalyzeAiReqDTO request);  // 발음 분석
}
