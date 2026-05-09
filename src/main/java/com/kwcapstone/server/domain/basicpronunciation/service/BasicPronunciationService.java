package com.kwcapstone.server.domain.basicpronunciation.service;

import com.kwcapstone.server.domain.basicpronunciation.dto.request.BasicPronunciationPracticeReqDTO;
import com.kwcapstone.server.domain.basicpronunciation.dto.response.BasicPronunciationPracticeResDTO;

public interface BasicPronunciationService {
    BasicPronunciationPracticeResDTO analyzePractice(BasicPronunciationPracticeReqDTO request);
}
