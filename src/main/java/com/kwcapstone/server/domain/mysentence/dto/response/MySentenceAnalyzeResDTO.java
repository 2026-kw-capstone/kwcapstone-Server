package com.kwcapstone.server.domain.mysentence.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class MySentenceAnalyzeResDTO {
    private Long analysisId;
    private Long sentenceId;
    private BigDecimal pronunciationScore;
    private BigDecimal speechRateScore;
    private BigDecimal silenceRatio;
    private String aiFeedback;

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