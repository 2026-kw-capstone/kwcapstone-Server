package com.kwcapstone.server.domain.scenario.service;

import com.kwcapstone.server.domain.scenario.dto.response.ScenarioDetailResDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioLevelResultResDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioListResDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioStepDetailResDTO;

public interface ScenarioQueryService {
    ScenarioListResDTO getScenarioList();  // 시나리오 목록 조히
    ScenarioDetailResDTO getScenarioDetail(Long scenarioId);  // 시나리오 상세 조회 및 레벨 조회
    ScenarioStepDetailResDTO getScenarioStep(Long scenarioId, Integer level, Integer stepNo);  // 시나리오 단계 조회
    ScenarioLevelResultResDTO getScenarioLevelResult(Long scenarioId, Integer level);  // 시나리오 결과
}
