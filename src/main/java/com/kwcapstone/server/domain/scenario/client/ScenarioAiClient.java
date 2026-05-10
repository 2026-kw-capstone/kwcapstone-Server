package com.kwcapstone.server.domain.scenario.client;

import com.kwcapstone.server.domain.scenario.dto.request.ScenarioGenerateAiReqDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioGenerateAiResDTO;

public interface ScenarioAiClient {
    ScenarioGenerateAiResDTO generateScenario(ScenarioGenerateAiReqDTO request);  // 시나리오 생성
}