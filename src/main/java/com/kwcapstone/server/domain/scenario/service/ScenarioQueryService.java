package com.kwcapstone.server.domain.scenario.service;

import com.kwcapstone.server.domain.scenario.dto.response.ScenarioDetailResDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioListResDTO;

public interface ScenarioQueryService {
    ScenarioListResDTO getScenarioList();  // 시나리오 목록 조히
    ScenarioDetailResDTO getScenarioDetail(Long scenarioId);  // 시나리오 상세 조회 및 레벨 조회
}
