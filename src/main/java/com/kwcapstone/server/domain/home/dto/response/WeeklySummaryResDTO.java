package com.kwcapstone.server.domain.home.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WeeklySummaryResDTO {
    private Integer weeklyTrainingCount;
    private Boolean isIncreasedFromLastWeek;
    private Integer averagePronunciationScore;
    private Integer averageMeaningDeliveryScore;
}
