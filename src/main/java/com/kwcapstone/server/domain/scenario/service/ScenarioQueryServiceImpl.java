package com.kwcapstone.server.domain.scenario.service;

import com.kwcapstone.server.domain.scenario.converter.ScenarioConverter;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioListResDTO;
import com.kwcapstone.server.domain.scenario.entity.Scenario;
import com.kwcapstone.server.domain.scenario.repository.ScenarioRepository;
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

    @Override
    public ScenarioListResDTO getScenarioList() {
        Long memberId = SecurityUtil.getCurrentMemberId();

        List<Scenario> scenarios =
                scenarioRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId);

        return ScenarioConverter.toListResponse(scenarios);
    }
}