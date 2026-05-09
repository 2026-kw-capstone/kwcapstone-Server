package com.kwcapstone.server.domain.basicpronunciation.client.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class VowelPracticeAiResDTO {
    private Boolean success;
    private String targetVowel;
    private BigDecimal pronunciationScore;
    private String pronunciationGrade;
    private String pronunciationLabel;
    private String feedback;
}
