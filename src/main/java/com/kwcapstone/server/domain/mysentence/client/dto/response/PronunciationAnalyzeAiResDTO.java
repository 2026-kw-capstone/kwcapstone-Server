package com.kwcapstone.server.domain.mysentence.client.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
public class PronunciationAnalyzeAiResDTO {

    private Boolean success;
    private String referenceText;
    private String sttText;
    private String acousticText;
    private BigDecimal pronunciationScore;
    private String feedback;
    private List<WordAnalysis> wordAnalysis;
    private VoiceAnalysis voiceAnalysis;

    @Getter
    @NoArgsConstructor
    public static class WordAnalysis {
        private String refChar;
        private String hypChar;
        private String grade;
    }

    @Getter
    @NoArgsConstructor
    public static class VoiceAnalysis {
        private SpeechRate speechRate;
        private SilenceRatio silenceRatio;
    }

    @Getter
    @NoArgsConstructor
    public static class SpeechRate {
        private BigDecimal syllablesPerSecond;
        private BigDecimal score;
        private String grade;
        private String label;
    }

    @Getter
    @NoArgsConstructor
    public static class SilenceRatio {
        private BigDecimal pausePercent;
        private String grade;
        private String label;
    }
}
