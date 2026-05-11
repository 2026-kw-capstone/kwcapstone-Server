package com.kwcapstone.server.domain.scenario.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScenarioPracticeAiReqDTO {
    private String s3Url;
    private String levelTitle;
    private String step;
    private String assistantMessage;
    private String userIntent;
}