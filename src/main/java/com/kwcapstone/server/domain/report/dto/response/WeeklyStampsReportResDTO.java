package com.kwcapstone.server.domain.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class WeeklyStampsReportResDTO {
    private LocalDate weekStartDate;
    private LocalDate weekEndDate;

    private Long totalStudyDays;
    private Long totalStydyCount;

    private List<Stamp> stamps;

    @Getter
    @AllArgsConstructor
    public static class Stamp {
        private LocalDate date;
        private String dayOfWeek;
        private String dayLabel;

        private Boolean hasStudy;
        private Long studyCount;

        private List<String> completedTypes;
    }
}
