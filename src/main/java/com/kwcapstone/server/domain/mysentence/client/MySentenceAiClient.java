package com.kwcapstone.server.domain.mysentence.client;

import com.kwcapstone.server.domain.mysentence.dto.request.MySentenceTtsReqDTO;

public interface MySentenceAiClient {
    byte[] requestTts(MySentenceTtsReqDTO request);
}
