package com.kwcapstone.server.domain.scenario.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScenarioStepDetailResDTO {

    private Long scenarioId;
    private Integer level;
    private Integer stepNo;
    private Long totalStepCount;

    private String levelTitle;
    private String step;
    private String assistantMessage;
    private String userIntent;

    private Boolean isAnswered;
}