package com.kwcapstone.server.domain.scenario.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ScenarioDetailResDTO {

    private Long scenarioId;
    private String title;
    private String description;
    private List<LevelInfo> levels;

    @Getter
    @AllArgsConstructor
    public static class LevelInfo {
        private Integer level;
        private String levelTitle;
        private String levelDescription;
    }
}
