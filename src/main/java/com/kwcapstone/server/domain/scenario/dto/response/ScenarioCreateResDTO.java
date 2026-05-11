package com.kwcapstone.server.domain.scenario.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioCreateResDTO {
    private Long scenarioId;
    private String title;
    private String description;
    private List<LevelInfo> levels;

    @Getter
    @AllArgsConstructor
    public static class LevelInfo{
        private int level;
        private String levelTitle;
        private String levelDescription;
        private List<StepInfo> steps;
    }

    @Getter
    @AllArgsConstructor
    public static class StepInfo{
        private int stepNo;
        private String step;
        private String assistantMessage;
        private String userIntent;
    }
}
