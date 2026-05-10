package com.kwcapstone.server.domain.basicpronunciation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class BasicPronunciationPracticeResDTO {
    private Long practiceId;
    private BigDecimal accuracyScore;
    private String feedback;
    private String voiceUrl;
    private String modelVoiceUrl;
}
