package com.kwcapstone.server.domain.scenario.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ScenarioGenerateAiResDTO {

    private Boolean success;
    private Data data;

    @Getter
    @NoArgsConstructor
    public static class Data {
        private String scenarioContext;
        private String goal;
        private List<Level> levels;
    }

    @Getter
    @NoArgsConstructor
    public static class Level {
        private String levelTitle;
        private String levelDescription;
        private List<Step> steps;
    }

    @Getter
    @NoArgsConstructor
    public static class Step {
        private String step;
        private String assistantMessage;
        private String userIntent;
    }
}