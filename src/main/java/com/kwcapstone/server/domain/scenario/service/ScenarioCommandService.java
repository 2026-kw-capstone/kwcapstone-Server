package com.kwcapstone.server.domain.scenario.service;

import com.kwcapstone.server.domain.scenario.dto.request.ScenarioCreateReqDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioCreateResDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioDeleteResDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioRegenerateResDTO;

public interface ScenarioCommandService {
    ScenarioCreateResDTO createScenario(ScenarioCreateReqDTO request);
    ScenarioRegenerateResDTO regenerateScenarioStep(Long scenarioId, Integer level, Integer stepNo);
    ScenarioDeleteResDTO deleteScenario(Long scenarioId);
}
