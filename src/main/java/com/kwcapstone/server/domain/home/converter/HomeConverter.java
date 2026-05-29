package com.kwcapstone.server.domain.home.converter;

import com.kwcapstone.server.domain.basicpronunciation.entity.BasicPronunciationPractice;
import com.kwcapstone.server.domain.conversation.entity.Conversation;
import com.kwcapstone.server.domain.home.dto.response.ContinueLearningResDTO;
import com.kwcapstone.server.domain.home.dto.response.WeeklySummaryResDTO;
import com.kwcapstone.server.domain.home.enums.ContinueLearningType;
import com.kwcapstone.server.domain.mysentence.entity.MySentence;
import com.kwcapstone.server.domain.scenario.entity.Scenario;
import com.kwcapstone.server.domain.scenario.entity.ScenarioAnalysisResult;
import com.kwcapstone.server.domain.scenario.entity.ScenarioLevel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HomeConverter {
    public static WeeklySummaryResDTO toWeeklySummaryResDTO(
            Long weeklyTrainingCount,
            Boolean isIncreasedFromLastWeek,
            Integer averagePronunciationScore,
            Integer averageMeaningDeliveryScore
    ) {
        return new WeeklySummaryResDTO(
                Math.toIntExact(weeklyTrainingCount),
                isIncreasedFromLastWeek,
                averagePronunciationScore,
                averageMeaningDeliveryScore
        );
    }

    public static ContinueLearningResDTO toContinueLearningResDTO(List<ContinueLearningResDTO.Content> contents) {
        return new ContinueLearningResDTO(contents);
    }

    public static ContinueLearningResDTO.Content toScenarioContent(ScenarioAnalysisResult result) {
        ScenarioLevel level = result.getScenarioStep().getScenarioLevel();
        Scenario scenario = level.getScenario();

        return new ContinueLearningResDTO.Content(
                ContinueLearningType.SCENARIO,
                result.getCreatedAt(),
                new ContinueLearningResDTO.ScenarioData(
                        scenario.getId(),
                        scenario.getTitle(),
                        level.getLevelNo()
                )
        );
    }

    public static ContinueLearningResDTO.Content toMySentenceContent(MySentence latestSentence, long sentenceCount) {
        return new ContinueLearningResDTO.Content(
                ContinueLearningType.MY_SENTENCE,
                latestSentence.getCreatedAt(),
                new ContinueLearningResDTO.MySentenceData(sentenceCount)
        );
    }

    public static ContinueLearningResDTO.Content toBasicPracticeContent(BasicPronunciationPractice practice) {
        return new ContinueLearningResDTO.Content(
                ContinueLearningType.BASIC_PRACTICE,
                practice.getCreatedAt(),
                new ContinueLearningResDTO.BasicPracticeData(practice.getTargetVowel().getKorean())
        );
    }

    public static ContinueLearningResDTO.Content toFreeTalkContent(Conversation conversation) {
        return new ContinueLearningResDTO.Content(
                ContinueLearningType.FREE_TALK,
                conversation.getLastMessageAt(),
                new ContinueLearningResDTO.FreeTalkData(
                        conversation.getId(),
                        conversation.getTitle()
                )
        );
    }
}
