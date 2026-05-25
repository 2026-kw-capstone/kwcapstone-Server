package com.kwcapstone.server.domain.scenario.client.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ScenarioRegenerateAiReqDTO {

    private String scenarioContext;
    private String goal;
    private List<Level> levels;
    private Integer targetLevelIndex;
    private Integer targetStepIndex;

    @Getter
    @AllArgsConstructor
    public static class Level {
        private String levelTitle;
        private String levelDescription;
        private List<Step> steps;
    }

    @Getter
    @AllArgsConstructor
    public static class Step {
        private String step;
        private String assistantMessage;
        private String userIntent;
    }
}
