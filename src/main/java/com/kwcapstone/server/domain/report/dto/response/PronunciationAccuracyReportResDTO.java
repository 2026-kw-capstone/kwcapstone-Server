package com.kwcapstone.server.domain.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class PronunciationAccuracyReportResDTO {
    private String period;
    private String type;
    private String typeLabel;

    private BigDecimal currentAverage;
    private BigDecimal previousAverage;
    private BigDecimal diff;

    private String currentLabel;
    private String previousLabel;

    private Boolean hasCurrentData;
    private Boolean hasPreviousData;

    private Range currentRange;
    private Range previousRange;

    @Getter
    @AllArgsConstructor
    public static class Range {
        private LocalDate startDate;
        private LocalDate endDate;
    }
}
