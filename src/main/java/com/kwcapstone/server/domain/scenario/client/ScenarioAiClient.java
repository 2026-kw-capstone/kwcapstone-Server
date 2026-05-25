package com.kwcapstone.server.domain.scenario.client;

import com.kwcapstone.server.domain.scenario.client.dto.request.ScenarioGenerateAiReqDTO;
import com.kwcapstone.server.domain.scenario.client.dto.request.ScenarioPracticeAiReqDTO;
import com.kwcapstone.server.domain.scenario.client.dto.request.ScenarioRegenerateAiReqDTO;
import com.kwcapstone.server.domain.scenario.client.dto.response.ScenarioGenerateAiResDTO;
import com.kwcapstone.server.domain.scenario.client.dto.response.ScenarioPracticeAiResDTO;

public interface ScenarioAiClient {
    ScenarioGenerateAiResDTO generateScenario(ScenarioGenerateAiReqDTO request);
    ScenarioGenerateAiResDTO regenerateScenarioStep(ScenarioRegenerateAiReqDTO request);
    ScenarioPracticeAiResDTO practiceScenario(ScenarioPracticeAiReqDTO request);
}