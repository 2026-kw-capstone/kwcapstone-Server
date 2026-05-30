package com.kwcapstone.server.domain.scenario.service;

import com.kwcapstone.server.domain.scenario.converter.ScenarioConverter;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioDetailResDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioLevelResultResDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioListResDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioStepDetailResDTO;
import com.kwcapstone.server.domain.scenario.entity.Scenario;
import com.kwcapstone.server.domain.scenario.entity.ScenarioAnalysisResult;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
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
                scenarioRepository.findAllByMemberIdAndDeletedAtIsNullOrderByCreatedAtDesc(memberId);

        return ScenarioConverter.toListResponse(scenarios);
    }

    @Override
    public ScenarioDetailResDTO getScenarioDetail(Long scenarioId) {
        Long memberId = SecurityUtil.getCurrentMemberId();

        Scenario scenario = scenarioRepository.findByIdAndDeletedAtIsNull(scenarioId)
                .orElseThrow(() -> new CustomException(ScenarioErrorCode.SCENARIO_NOT_FOUND));

        if (!scenario.getMember().getId().equals(memberId)) {
            throw new CustomException(ScenarioErrorCode.SCENARIO_FORBIDDEN);
        }

        List<ScenarioLevel> levels =
                scenarioLevelRepository.findAllByScenarioIdAndDeletedAtIsNullOrderByLevelNoAsc(scenarioId);

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

        Scenario scenario = scenarioRepository.findByIdAndDeletedAtIsNull(scenarioId)
                .orElseThrow(() -> new CustomException(ScenarioErrorCode.SCENARIO_NOT_FOUND));

        if (!scenario.getMember().getId().equals(memberId)) {
            throw new CustomException(ScenarioErrorCode.SCENARIO_FORBIDDEN);
        }

        ScenarioLevel scenarioLevel = scenarioLevelRepository
                .findByScenarioIdAndLevelNoAndDeletedAtIsNull(scenarioId, level)
                .orElseThrow(() -> new CustomException(ScenarioErrorCode.SCENARIO_STEP_NOT_FOUND));

        ScenarioStep scenarioStep = scenarioStepRepository
                .findByScenarioLevelIdAndStepNoAndDeletedAtIsNull(scenarioLevel.getId(), stepNo)
                .orElseThrow(() -> new CustomException(ScenarioErrorCode.SCENARIO_STEP_NOT_FOUND));

        Long totalStepCount = scenarioStepRepository.countByScenarioLevelIdAndDeletedAtIsNull(scenarioLevel.getId());

        Boolean isAnswered = scenarioAnalysisResultRepository
                .existsByScenarioStepIdAndMemberIdAndDeletedAtIsNull(scenarioStep.getId(), memberId);

        return ScenarioConverter.toStepDetailResponse(
                scenario,
                scenarioLevel,
                scenarioStep,
                totalStepCount,
                isAnswered
        );
    }

    @Override
    public ScenarioLevelResultResDTO getScenarioLevelResult(
            Long scenarioId,
            Integer level
    ) {
        validateLevel(level);

        Long memberId = SecurityUtil.getCurrentMemberId();

        Scenario scenario = scenarioRepository.findByIdAndDeletedAtIsNull(scenarioId)
                .orElseThrow(() -> new CustomException(ScenarioErrorCode.SCENARIO_NOT_FOUND));

        if (!scenario.getMember().getId().equals(memberId)) {
            throw new CustomException(ScenarioErrorCode.SCENARIO_FORBIDDEN);
        }

        ScenarioLevel scenarioLevel = scenarioLevelRepository
                .findByScenarioIdAndLevelNoAndDeletedAtIsNull(scenarioId, level)
                .orElseThrow(() -> new CustomException(ScenarioErrorCode.SCENARIO_RESULT_NOT_FOUND));

        List<ScenarioStep> steps =
                scenarioStepRepository.findAllByScenarioLevelIdAndDeletedAtIsNullOrderByStepNoAsc(
                        scenarioLevel.getId()
                );

        if (steps.isEmpty()) {
            throw new CustomException(ScenarioErrorCode.SCENARIO_RESULT_NOT_FOUND);
        }

        List<ScenarioAnalysisResult> results = steps.stream()
                .map(step -> scenarioAnalysisResultRepository
                        .findTopByScenarioStepIdAndMemberIdAndDeletedAtIsNullOrderByCreatedAtDesc(
                                step.getId(),
                                memberId
                        )
                        .orElse(null)
                )
                .filter(result -> result != null)
                .toList();

        if (results.isEmpty()) {
            throw new CustomException(ScenarioErrorCode.SCENARIO_RESULT_NOT_FOUND);
        }

        int totalStepCount = steps.size();
        int completedStepCount = results.size();

        BigDecimal averagePronunciationScore = calculateAveragePronunciationScore(results);
        BigDecimal averageMeaningDeliveryScore = calculateAverageMeaningDeliveryScore(results);

        return new ScenarioLevelResultResDTO(
                scenario.getId(),
                level,
                totalStepCount,
                completedStepCount,
                averagePronunciationScore,
                averageMeaningDeliveryScore,
                completedStepCount == totalStepCount
        );
    }

    // level 값이 1~3 범위 내에 있는지 검증
    private void validateLevel(Integer level) {
        if (level == null || level < 1 || level > 3) {
            throw new CustomException(ScenarioErrorCode.INVALID_LEVEL);
        }
    }

    // step 값이 1~3 범위 내에 있는지 검증
    private void validateStep(Integer stepNo) {
        if (stepNo == null || stepNo < 1 || stepNo > 3) {
            throw new CustomException(ScenarioErrorCode.INVALID_STEP);
        }
    }

    // 평균 발음 정확도 계산
    private BigDecimal calculateAveragePronunciationScore(
            List<ScenarioAnalysisResult> results
    ) {
        BigDecimal sum = results.stream()
                .map(ScenarioAnalysisResult::getPronunciationScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sum.divide(
                BigDecimal.valueOf(results.size()),
                0,
                RoundingMode.HALF_UP
        );
    }

    // 평균 의미 전달률 계산
    private BigDecimal calculateAverageMeaningDeliveryScore(
            List<ScenarioAnalysisResult> results
    ) {
        BigDecimal sum = results.stream()
                .map(ScenarioAnalysisResult::getMeaningDeliveryScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sum.divide(
                BigDecimal.valueOf(results.size()),
                0,
                RoundingMode.HALF_UP
        );
    }
}