package com.kwcapstone.server.domain.scenario.service;

import com.kwcapstone.server.domain.member.entity.Member;
import com.kwcapstone.server.domain.member.repository.MemberRepository;
import com.kwcapstone.server.domain.scenario.client.ScenarioAiClient;
import com.kwcapstone.server.domain.scenario.client.dto.request.ScenarioGenerateAiReqDTO;
import com.kwcapstone.server.domain.scenario.client.dto.request.ScenarioRegenerateAiReqDTO;
import com.kwcapstone.server.domain.scenario.client.dto.response.ScenarioGenerateAiResDTO;
import com.kwcapstone.server.domain.scenario.converter.ScenarioConverter;
import com.kwcapstone.server.domain.scenario.dto.request.ScenarioCreateReqDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioCreateResDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioDeleteResDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioRegenerateResDTO;
import com.kwcapstone.server.domain.scenario.entity.Scenario;
import com.kwcapstone.server.domain.scenario.entity.ScenarioLevel;
import com.kwcapstone.server.domain.scenario.entity.ScenarioStep;
import com.kwcapstone.server.domain.scenario.exception.code.ScenarioErrorCode;
import com.kwcapstone.server.domain.scenario.repository.ScenarioAnalysisResultRepository;
import com.kwcapstone.server.domain.scenario.repository.ScenarioLevelRepository;
import com.kwcapstone.server.domain.scenario.repository.ScenarioRepository;
import com.kwcapstone.server.domain.scenario.repository.ScenarioStepRepository;
import com.kwcapstone.server.global.apiPayload.exception.CustomException;
import com.kwcapstone.server.global.apiPayload.response.ErrorCode;
import com.kwcapstone.server.global.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ScenarioCommandServiceImpl implements ScenarioCommandService {

    private static final int SCENARIO_LEVEL_COUNT = 3;
    private static final int SCENARIO_STEP_COUNT = 3;

    private final ScenarioRepository scenarioRepository;
    private final ScenarioLevelRepository scenarioLevelRepository;
    private final ScenarioStepRepository scenarioStepRepository;
    private final ScenarioAnalysisResultRepository scenarioAnalysisResultRepository;
    private final MemberRepository memberRepository;
    private final ScenarioAiClient scenarioAiClient;

    @Override
    public ScenarioCreateResDTO createScenario(ScenarioCreateReqDTO request) {
        validateCreateRequest(request);

        Long memberId = SecurityUtil.getCurrentMemberId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        ScenarioGenerateAiResDTO aiResponse = scenarioAiClient.generateScenario(
                new ScenarioGenerateAiReqDTO(
                        request.getTitle().trim(),
                        request.getDescription().trim()
                )
        );

        validateAiResponse(
                aiResponse,
                () -> new CustomException(ScenarioErrorCode.SCENARIO_GENERATION_FAILED)
        );

        Scenario scenario = ScenarioConverter.toScenario(request, member, aiResponse);
        Scenario savedScenario = scenarioRepository.save(scenario);

        return ScenarioConverter.toCreateResponse(savedScenario);
    }

    @Override
    public ScenarioRegenerateResDTO regenerateScenarioStep(
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

        List<ScenarioLevel> scenarioLevels =
                scenarioLevelRepository.findAllByScenarioIdAndDeletedAtIsNullOrderByLevelNoAsc(scenarioId);

        Map<Integer, List<ScenarioStep>> stepsByLevelNo =
                loadStepsByLevelNo(scenarioLevels);

        validateScenarioStructure(scenarioLevels, stepsByLevelNo);

        ScenarioGenerateAiResDTO aiResponse =
                scenarioAiClient.regenerateScenarioStep(
                        buildRegenerateAiRequest(
                                scenario,
                                scenarioLevels,
                                stepsByLevelNo,
                                level,
                                stepNo
                        )
                );

        validateAiResponse(
                aiResponse,
                () -> new CustomException(ScenarioErrorCode.SCENARIO_REGENERATION_FAILED)
        );

        applyRegeneratedContent(
                scenarioLevels,
                stepsByLevelNo,
                aiResponse,
                level,
                stepNo
        );

        List<Long> affectedStepIds =
                collectAffectedStepIds(stepsByLevelNo, level, stepNo);

        if (!affectedStepIds.isEmpty()) {
            scenarioAnalysisResultRepository.deleteAllByMemberIdAndScenarioStepIdIn(
                    memberId,
                    affectedStepIds
            );
        }

        ScenarioLevel currentLevel = scenarioLevels.get(level - 1);
        ScenarioStep currentStep = stepsByLevelNo.get(level).get(stepNo - 1);

        return new ScenarioRegenerateResDTO(
                scenario.getId(),
                scenario.getTitle(),
                scenario.getDescription(),
                new ScenarioRegenerateResDTO.RegeneratedFrom(level, stepNo),
                new ScenarioRegenerateResDTO.CurrentStep(
                        scenario.getId(),
                        level,
                        stepNo,
                        currentLevel.getLevelTitle(),
                        currentStep.getStepName(),
                        currentStep.getAssistantMessage(),
                        currentStep.getUserIntent(),
                        false
                )
        );
    }

    @Override
    public ScenarioDeleteResDTO deleteScenario(Long scenarioId) {
        Long memberId = SecurityUtil.getCurrentMemberId();

        Scenario scenario = scenarioRepository.findByIdAndDeletedAtIsNull(scenarioId)
                .orElseThrow(() -> new CustomException(ScenarioErrorCode.SCENARIO_NOT_FOUND));

        if (!scenario.getMember().getId().equals(memberId)) {
            throw new CustomException(ScenarioErrorCode.SCENARIO_FORBIDDEN);
        }

        LocalDateTime deletedAt = LocalDateTime.now();

        scenarioAnalysisResultRepository.softDeleteAllByScenarioId(scenarioId, deletedAt);
        scenarioStepRepository.softDeleteAllByScenarioId(scenarioId, deletedAt);
        scenarioLevelRepository.softDeleteAllByScenarioId(scenarioId, deletedAt);
        scenario.softDelete();
        scenarioRepository.save(scenario);

        return new ScenarioDeleteResDTO(scenario.getId());
    }

    private void validateCreateRequest(ScenarioCreateReqDTO request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new CustomException(ScenarioErrorCode.EMPTY_SCENARIO_TITLE);
        }

        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new CustomException(ScenarioErrorCode.EMPTY_SCENARIO_DESCRIPTION);
        }
    }

    private void validateLevel(Integer level) {
        if (level == null || level < 1 || level > SCENARIO_LEVEL_COUNT) {
            throw new CustomException(ScenarioErrorCode.INVALID_LEVEL);
        }
    }

    private void validateStep(Integer stepNo) {
        if (stepNo == null || stepNo < 1 || stepNo > SCENARIO_STEP_COUNT) {
            throw new CustomException(ScenarioErrorCode.INVALID_STEP);
        }
    }

    private Map<Integer, List<ScenarioStep>> loadStepsByLevelNo(
            List<ScenarioLevel> scenarioLevels
    ) {
        return scenarioLevels.stream()
                .collect(Collectors.toMap(
                        ScenarioLevel::getLevelNo,
                        scenarioLevel -> scenarioStepRepository
                                .findAllByScenarioLevelIdAndDeletedAtIsNullOrderByStepNoAsc(scenarioLevel.getId())
                ));
    }

    private void validateScenarioStructure(
            List<ScenarioLevel> scenarioLevels,
            Map<Integer, List<ScenarioStep>> stepsByLevelNo
    ) {
        if (scenarioLevels.size() != SCENARIO_LEVEL_COUNT) {
            throw new CustomException(ScenarioErrorCode.SCENARIO_STEP_NOT_FOUND);
        }

        boolean hasInvalidStepCount = scenarioLevels.stream()
                .anyMatch(scenarioLevel -> stepsByLevelNo
                        .getOrDefault(scenarioLevel.getLevelNo(), List.of())
                        .size() != SCENARIO_STEP_COUNT);

        if (hasInvalidStepCount) {
            throw new CustomException(ScenarioErrorCode.SCENARIO_STEP_NOT_FOUND);
        }
    }

    private ScenarioRegenerateAiReqDTO buildRegenerateAiRequest(
            Scenario scenario,
            List<ScenarioLevel> scenarioLevels,
            Map<Integer, List<ScenarioStep>> stepsByLevelNo,
            Integer level,
            Integer stepNo
    ) {
        List<ScenarioRegenerateAiReqDTO.Level> levels = scenarioLevels.stream()
                .sorted(Comparator.comparing(ScenarioLevel::getLevelNo))
                .map(scenarioLevel -> new ScenarioRegenerateAiReqDTO.Level(
                        scenarioLevel.getLevelTitle(),
                        scenarioLevel.getLevelDescription(),
                        stepsByLevelNo.get(scenarioLevel.getLevelNo()).stream()
                                .sorted(Comparator.comparing(ScenarioStep::getStepNo))
                                .map(scenarioStep -> new ScenarioRegenerateAiReqDTO.Step(
                                        scenarioStep.getStepName(),
                                        scenarioStep.getAssistantMessage(),
                                        scenarioStep.getUserIntent()
                                ))
                                .toList()
                ))
                .toList();

        return new ScenarioRegenerateAiReqDTO(
                scenario.getScenarioContext(),
                scenario.getGoal(),
                levels,
                level - 1,
                stepNo - 1
        );
    }

    private void validateAiResponse(
            ScenarioGenerateAiResDTO aiResponse,
            Supplier<CustomException> exceptionSupplier
    ) {
        if (aiResponse == null || !Boolean.TRUE.equals(aiResponse.getSuccess())) {
            throw exceptionSupplier.get();
        }

        if (aiResponse.getData() == null || aiResponse.getData().getLevels() == null) {
            throw exceptionSupplier.get();
        }

        if (aiResponse.getData().getLevels().size() != SCENARIO_LEVEL_COUNT) {
            throw exceptionSupplier.get();
        }

        boolean hasInvalidStepCount = aiResponse.getData().getLevels().stream()
                .anyMatch(level -> level.getSteps() == null
                        || level.getSteps().size() != SCENARIO_STEP_COUNT);

        if (hasInvalidStepCount) {
            throw exceptionSupplier.get();
        }
    }

    private void applyRegeneratedContent(
            List<ScenarioLevel> scenarioLevels,
            Map<Integer, List<ScenarioStep>> stepsByLevelNo,
            ScenarioGenerateAiResDTO aiResponse,
            Integer targetLevel,
            Integer targetStepNo
    ) {
        List<ScenarioGenerateAiResDTO.Level> aiLevels =
                aiResponse.getData().getLevels();

        for (int levelIndex = 0; levelIndex < scenarioLevels.size(); levelIndex++) {
            ScenarioLevel scenarioLevel = scenarioLevels.get(levelIndex);
            ScenarioGenerateAiResDTO.Level aiLevel = aiLevels.get(levelIndex);

            if (scenarioLevel.getLevelNo() > targetLevel) {
                scenarioLevel.updateContent(
                        aiLevel.getLevelTitle(),
                        aiLevel.getLevelDescription()
                );
            }

            List<ScenarioStep> scenarioSteps =
                    stepsByLevelNo.get(scenarioLevel.getLevelNo());
            List<ScenarioGenerateAiResDTO.Step> aiSteps = aiLevel.getSteps();

            for (int stepIndex = 0; stepIndex < scenarioSteps.size(); stepIndex++) {
                ScenarioStep scenarioStep = scenarioSteps.get(stepIndex);

                if (isAffectedStep(
                        scenarioLevel.getLevelNo(),
                        scenarioStep.getStepNo(),
                        targetLevel,
                        targetStepNo
                )) {
                    ScenarioGenerateAiResDTO.Step aiStep = aiSteps.get(stepIndex);
                    scenarioStep.updateContent(
                            aiStep.getStep(),
                            aiStep.getAssistantMessage(),
                            aiStep.getUserIntent()
                    );
                }
            }
        }
    }

    private List<Long> collectAffectedStepIds(
            Map<Integer, List<ScenarioStep>> stepsByLevelNo,
            Integer targetLevel,
            Integer targetStepNo
    ) {
        List<Long> affectedStepIds = new ArrayList<>();

        stepsByLevelNo.forEach((levelNo, steps) -> steps.stream()
                .filter(step -> isAffectedStep(
                        levelNo,
                        step.getStepNo(),
                        targetLevel,
                        targetStepNo
                ))
                .map(ScenarioStep::getId)
                .forEach(affectedStepIds::add));

        return affectedStepIds;
    }

    private boolean isAffectedStep(
            Integer levelNo,
            Integer stepNo,
            Integer targetLevel,
            Integer targetStepNo
    ) {
        return levelNo > targetLevel
                || levelNo.equals(targetLevel) && stepNo >= targetStepNo;
    }
}