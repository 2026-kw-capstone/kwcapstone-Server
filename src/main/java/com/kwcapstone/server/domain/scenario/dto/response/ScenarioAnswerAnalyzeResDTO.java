package com.kwcapstone.server.domain.scenario.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class ScenarioAnswerAnalyzeResDTO {

    private Long answerId;
    private Long scenarioId;

    private Integer level;
    private Integer stepNo;

    private BigDecimal pronunciationScore;
    private BigDecimal meaningDeliveryScore;
    private BigDecimal speechRateScore;
    private BigDecimal silenceRatio;

    private String feedback;

    private Boolean isLastStep;
    private Integer nextStepNo;

    private List<WordAnalysis> wordAnalysis;

    @Getter
    @AllArgsConstructor
    public static class WordAnalysis {
        private String refChar;
        private String hypChar;
        private String grade;
    }
}