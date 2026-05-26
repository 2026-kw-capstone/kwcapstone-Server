package com.kwcapstone.server.domain.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class AchievementTrendReportResDTO {
    private String period;

    private LocalDate startDate;
    private LocalDate endDate;

    private List<Point> points;

    @Getter
    @AllArgsConstructor
    public static class Point {
        private String label;

        private LocalDate startDate;
        private LocalDate endDate;

        private BigDecimal pronunciationAccuracy;
        private BigDecimal meaningDeliveryRate;

        private Boolean hasPronunciationData;
        private Boolean hasMeaningDeliveryData;
    }
}
