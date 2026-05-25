package com.kwcapstone.server.domain.scenario.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class ScenarioAnswerAnalyzeResDTO {

    private Long answerId;
    private Long scenarioId;

    private Integer level;
    private Integer stepNo;

    private BigDecimal meaningDeliveryScore;
    private String meaningFeedback;

    private BigDecimal pronunciationScore;
    private BigDecimal speechRateScore;
    private BigDecimal silenceRatio;
    private String pronunciationFeedback;

    private Boolean isLastStep;
    private Integer nextStepNo;

    private List<WordAnalysis> wordAnalysis;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WordAnalysis {
        private String refChar;
        private String hypChar;
        private String grade;
    }
}