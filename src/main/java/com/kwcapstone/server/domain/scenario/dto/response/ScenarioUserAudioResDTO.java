package com.kwcapstone.server.domain.scenario.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScenarioUserAudioResDTO {
    private Long answerId;
    private Long scenarioId;
    private Integer level;
    private Integer stepNo;
    private String userAudioUrl;
    private Integer expiresIn;
}