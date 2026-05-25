package com.kwcapstone.server.domain.scenario.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScenarioRegenerateResDTO {

    private Long scenarioId;
    private String title;
    private String description;
    private RegeneratedFrom regeneratedFrom;
    private CurrentStep currentStep;

    @Getter
    @AllArgsConstructor
    public static class RegeneratedFrom {
        private Integer level;
        private Integer stepNo;
    }

    @Getter
    @AllArgsConstructor
    public static class CurrentStep {
        private Long scenarioId;
        private Integer level;
        private Integer stepNo;
        private String levelTitle;
        private String step;
        private String assistantMessage;
        private String userIntent;
        private Boolean isAnswered;
    }
}
