package com.kwcapstone.server.domain.scenario.service;

import com.kwcapstone.server.domain.scenario.converter.ScenarioConverter;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioDetailResDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioListResDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioStepDetailResDTO;
import com.kwcapstone.server.domain.scenario.entity.Scenario;
import com.kwcapstone.server.domain.scenario.entity.ScenarioLevel;
import com.kwcapstone.server.domain.scenario.entity.ScenarioStep;
import com.kwcapstone.server.domain.scenario.exception.code.ScenarioErrorCode;
import com.kwcapstone.server.domain.scenario.repository.ScenarioAnalysisResultRepository;
import com.kwcapstone.server.domain.scenario.repository.ScenarioLevelRepository;
import com.kwcapstone.server.domain.scenario.repository.ScenarioRepository;
import com.kwcapstone.server.domain.scenario.repository.ScenarioStepRepository;
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
    private final ScenarioStepRepository scenarioStepRepository;
    private final ScenarioAnalysisResultRepository scenarioAnalysisResultRepository;

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

    @Override
    public ScenarioStepDetailResDTO getScenarioStep(
            Long scenarioId,
            Integer level,
            Integer stepNo
    ) {
        validateLevel(level);
        validateStep(stepNo);

        Long memberId = SecurityUtil.getCurrentMemberId();

        Scenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new CustomException(ScenarioErrorCode.SCENARIO_NOT_FOUND));

        if (!scenario.getMember().getId().equals(memberId)) {
            throw new CustomException(ScenarioErrorCode.SCENARIO_FORBIDDEN);
        }

        ScenarioLevel scenarioLevel = scenarioLevelRepository
                .findByScenarioIdAndLevelNo(scenarioId, level)
                .orElseThrow(() -> new CustomException(ScenarioErrorCode.SCENARIO_STEP_NOT_FOUND));

        ScenarioStep scenarioStep = scenarioStepRepository
                .findByScenarioLevelIdAndStepNo(scenarioLevel.getId(), stepNo)
                .orElseThrow(() -> new CustomException(ScenarioErrorCode.SCENARIO_STEP_NOT_FOUND));

        Long totalStepCount = scenarioStepRepository.countByScenarioLevelId(scenarioLevel.getId());

        Boolean isAnswered = scenarioAnalysisResultRepository
                .existsByScenarioStepIdAndMemberId(scenarioStep.getId(), memberId);

        return ScenarioConverter.toStepDetailResponse(
                scenario,
                scenarioLevel,
                scenarioStep,
                totalStepCount,
                isAnswered
        );
    }

    private void validateLevel(Integer level) {
        if (level == null || level < 1 || level > 3) {
            throw new CustomException(ScenarioErrorCode.INVALID_LEVEL);
        }
    }

    private void validateStep(Integer stepNo) {
        if (stepNo == null || stepNo < 1 || stepNo > 3) {
            throw new CustomException(ScenarioErrorCode.INVALID_STEP);
        }
    }
}