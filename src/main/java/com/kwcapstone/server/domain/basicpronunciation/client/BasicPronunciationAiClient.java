package com.kwcapstone.server.domain.basicpronunciation.client;

import com.kwcapstone.server.domain.basicpronunciation.client.dto.request.VowelPracticeAiReqDTO;
import com.kwcapstone.server.domain.basicpronunciation.client.dto.response.VowelPracticeAiResDTO;

public interface BasicPronunciationAiClient {
    VowelPracticeAiResDTO analyzeVowel(VowelPracticeAiReqDTO request);
    byte[] synthesizeSpeech(String text);
}
