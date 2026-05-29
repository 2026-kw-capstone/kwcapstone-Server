package com.kwcapstone.server.domain.home.converter;

import com.kwcapstone.server.domain.home.dto.response.WeeklySummaryResDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
}
