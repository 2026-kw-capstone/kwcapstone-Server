package com.kwcapstone.server.domain.basicpronunciation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BasicPronunciationLatestResDTO {
    private Boolean hasPractice;
    private Practice practice;

    @Getter
    @AllArgsConstructor
    public static class Practice {
        private Long practiceId;
        private BigDecimal accuracyScore;
        private String feedback;
        private String voiceUrl;
        private String modelVoiceUrl;
        private LocalDateTime createdAt;
    }
}
