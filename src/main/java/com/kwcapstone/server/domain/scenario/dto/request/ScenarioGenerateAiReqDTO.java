package com.kwcapstone.server.domain.scenario.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScenarioGenerateAiReqDTO {
    private String scenarioContext;
    private String goal;
}