package com.kwcapstone.server.domain.scenario.client;

import com.kwcapstone.server.domain.scenario.client.dto.request.ScenarioGenerateAiReqDTO;
import com.kwcapstone.server.domain.scenario.client.dto.request.ScenarioPracticeAiReqDTO;
import com.kwcapstone.server.domain.scenario.client.dto.response.ScenarioGenerateAiResDTO;
import com.kwcapstone.server.domain.scenario.client.dto.response.ScenarioPracticeAiResDTO;

public interface ScenarioAiClient {
    ScenarioGenerateAiResDTO generateScenario(ScenarioGenerateAiReqDTO request);  // 시나리오 생성
    ScenarioPracticeAiResDTO practiceScenario(ScenarioPracticeAiReqDTO request);  // 음성 파일 분석
}