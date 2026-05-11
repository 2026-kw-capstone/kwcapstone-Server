package com.kwcapstone.server.domain.scenario.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioListResDTO {
    private List<ScenarioInfo> scenarios;

    @Getter
    @AllArgsConstructor
    public static class ScenarioInfo {
        private Long scenarioId;
        private String title;
        private String description;
    }
}