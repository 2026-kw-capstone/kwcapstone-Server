package com.kwcapstone.server.domain.basicpronunciation.service;

import com.kwcapstone.server.domain.basicpronunciation.dto.request.BasicPronunciationPracticeReqDTO;
import com.kwcapstone.server.domain.basicpronunciation.dto.response.BasicPronunciationLatestResDTO;
import com.kwcapstone.server.domain.basicpronunciation.dto.response.BasicPronunciationPracticeResDTO;
import com.kwcapstone.server.domain.basicpronunciation.enums.BasicVowel;

public interface BasicPronunciationService {
    BasicPronunciationPracticeResDTO analyzePractice(BasicPronunciationPracticeReqDTO request);
    BasicPronunciationLatestResDTO getLatestPractice(BasicVowel targetVowel);
}
