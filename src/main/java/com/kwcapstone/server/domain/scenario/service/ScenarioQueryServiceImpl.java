package com.kwcapstone.server.domain.scenario.service;

import com.kwcapstone.server.domain.scenario.converter.ScenarioConverter;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioDetailResDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioListResDTO;
import com.kwcapstone.server.domain.scenario.entity.Scenario;
import com.kwcapstone.server.domain.scenario.entity.ScenarioLevel;
import com.kwcapstone.server.domain.scenario.exception.code.ScenarioErrorCode;
import com.kwcapstone.server.domain.scenario.repository.ScenarioLevelRepository;
import com.kwcapstone.server.domain.scenario.repository.ScenarioRepository;
import com.kwcapstone.server.global.apiPayload.exception.CustomException;
import com.kwcapstone.server.global.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScenarioQueryServiceImpl implements ScenarioQueryService {

    private final ScenarioRepository scenarioRepository;
    private final ScenarioLevelRepository scenarioLevelRepository;

    @Override
    public ScenarioListResDTO getScenarioList() {
        Long memberId = SecurityUtil.getCurrentMemberId();

        List<Scenario> scenarios =
                scenarioRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId);

        return ScenarioConverter.toListResponse(scenarios);
    }

    @Override
    public ScenarioDetailResDTO getScenarioDetail(Long scenarioId) {
        Long memberId = SecurityUtil.getCurrentMemberId();

        Scenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new CustomException(ScenarioErrorCode.SCENARIO_NOT_FOUND));

        if (!scenario.getMember().getId().equals(memberId)) {
            throw new CustomException(ScenarioErrorCode.SCENARIO_FORBIDDEN);
        }

        List<ScenarioLevel> levels =
                scenarioLevelRepository.findAllByScenarioIdOrderByLevelNoAsc(scenarioId);

        return ScenarioConverter.toDetailResponse(scenario, levels);
    }
}