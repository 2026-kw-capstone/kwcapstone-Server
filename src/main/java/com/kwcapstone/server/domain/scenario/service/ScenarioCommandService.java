package com.kwcapstone.server.domain.scenario.service;

import com.kwcapstone.server.domain.scenario.dto.request.ScenarioCreateReqDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioCreateResDTO;

public interface ScenarioCommandService {
    ScenarioCreateResDTO createScenario(ScenarioCreateReqDTO request);  // 시나리오 생성
}
