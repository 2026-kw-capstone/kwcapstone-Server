package com.kwcapstone.server.domain.scenario.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ScenarioLevelResultResDTO {
    private Long scenarioId;
    private Integer level;
    private Integer totalStepCount;
    private Integer completedStepCount;
    private BigDecimal averagePronunciationScore;
    private BigDecimal averageMeaningDeliveryScore;
    private Boolean isCompleted;
}