package com.kwcapstone.server.domain.scenario.converter;

import com.kwcapstone.server.domain.member.entity.Member;
import com.kwcapstone.server.domain.scenario.dto.request.ScenarioCreateReqDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioCreateResDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioGenerateAiResDTO;
import com.kwcapstone.server.domain.scenario.entity.Scenario;
import com.kwcapstone.server.domain.scenario.entity.ScenarioLevel;
import com.kwcapstone.server.domain.scenario.entity.ScenarioStep;

import java.util.List;
import java.util.stream.IntStream;

public class ScenarioConverter {

    private ScenarioConverter() {
    }

    public static Scenario toScenario(
            ScenarioCreateReqDTO request,
            Member member,
            ScenarioGenerateAiResDTO aiResponse
    ) {
        Scenario scenario = Scenario.builder()
                .member(member)
                .title(request.getTitle().trim())
                .description(request.getDescription().trim())
                .scenarioContext(aiResponse.getData().getScenarioContext())
                .goal(aiResponse.getData().getGoal())
                .build();

        List<ScenarioGenerateAiResDTO.Level> aiLevels = aiResponse.getData().getLevels();

        IntStream.range(0, aiLevels.size()).forEach(levelIndex -> {
            ScenarioGenerateAiResDTO.Level aiLevel = aiLevels.get(levelIndex);

            ScenarioLevel level = ScenarioLevel.builder()
                    .levelNo(levelIndex + 1)
                    .levelTitle(aiLevel.getLevelTitle())
                    .levelDescription(aiLevel.getLevelDescription())
                    .build();

            List<ScenarioGenerateAiResDTO.Step> aiSteps = aiLevel.getSteps();

            IntStream.range(0, aiSteps.size()).forEach(stepIndex -> {
                ScenarioGenerateAiResDTO.Step aiStep = aiSteps.get(stepIndex);

                ScenarioStep step = ScenarioStep.builder()
                        .stepNo(stepIndex + 1)
                        .stepName(aiStep.getStep())
                        .assistantMessage(aiStep.getAssistantMessage())
                        .userIntent(aiStep.getUserIntent())
                        .build();

                level.addStep(step);
            });

            scenario.addLevel(level);
        });

        return scenario;
    }

    public static ScenarioCreateResDTO toCreateResponse(Scenario scenario) {
        List<ScenarioCreateResDTO.LevelInfo> levels = scenario.getLevels().stream()
                .map(level -> new ScenarioCreateResDTO.LevelInfo(
                        level.getLevelNo(),
                        level.getLevelTitle(),
                        level.getLevelDescription(),
                        level.getSteps().stream()
                                .map(step -> new ScenarioCreateResDTO.StepInfo(
                                        step.getStepNo(),
                                        step.getStepName(),
                                        step.getAssistantMessage(),
                                        step.getUserIntent()
                                ))
                                .toList()
                ))
                .toList();

        return new ScenarioCreateResDTO(
                scenario.getId(),
                scenario.getTitle(),
                scenario.getDescription(),
                levels
        );
    }
}