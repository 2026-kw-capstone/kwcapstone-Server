package com.kwcapstone.server.domain.home.dto.response;

import com.kwcapstone.server.domain.home.enums.ContinueLearningType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ContinueLearningResDTO {
    private List<Content> contents;

    @Getter
    @AllArgsConstructor
    public static class Content {
        private ContinueLearningType type;
        private LocalDateTime latestCreatedAt;
        private Object data;
    }

    @Getter
    @AllArgsConstructor
    public static class ScenarioData {
        private Long scenarioId;
        private String scenarioTitle;
        private Integer currentLevel;
    }

    @Getter
    @AllArgsConstructor
    public static class MySentenceData {
        private Long sentenceCount;
    }

    @Getter
    @AllArgsConstructor
    public static class BasicPracticeData {
        private String practiceText;
    }

    @Getter
    @AllArgsConstructor
    public static class FreeTalkData {
        private Long conversationId;
        private String conversationTitle;
    }
}
