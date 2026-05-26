package com.kwcapstone.server.domain.report.service;

import com.kwcapstone.server.domain.basicpronunciation.repository.BasicPronunciationPracticeRepository;
import com.kwcapstone.server.domain.mysentence.repository.MySentenceAnalysisResultRepository;
import com.kwcapstone.server.domain.report.dto.response.PronunciationAccuracyReportResDTO;
import com.kwcapstone.server.domain.report.enums.ReportPeriod;
import com.kwcapstone.server.domain.report.enums.ReportPracticeType;
import com.kwcapstone.server.domain.scenario.repository.ScenarioAnalysisResultRepository;
import com.kwcapstone.server.global.apiPayload.exception.CustomException;
import com.kwcapstone.server.global.apiPayload.response.ErrorCode;
import com.kwcapstone.server.global.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {
    private final MySentenceAnalysisResultRepository mySentenceAnalysisResultRepository;
    private final BasicPronunciationPracticeRepository basicPronunciationPracticeRepository;
    private final ScenarioAnalysisResultRepository scenarioAnalysisResultRepository;

    @Override
    public PronunciationAccuracyReportResDTO getPronunciationAccuracy(String period, String type, String baseDate) {
        Long memberId = SecurityUtil.getCurrentMemberId();

        ReportPeriod reportPeriod = ReportPeriod.from(period);
        ReportPracticeType practiceType = ReportPracticeType.from(type);
        LocalDate referenceDate = parseBaseDate(baseDate);

        DateRange currentRange = calculateCurrentRange(reportPeriod, referenceDate);
        DateRange previousRange = calculatePreviousRange(reportPeriod, currentRange);

        BigDecimal currentAverage = findAverage(memberId, practiceType, currentRange);
        BigDecimal previousAverage = findAverage(memberId, practiceType, previousRange);
        BigDecimal diff = calculateDiff(currentAverage, previousAverage);

        return new PronunciationAccuracyReportResDTO(
                reportPeriod.name(),
                practiceType.name(),
                practiceType.getTypeLabel(),
                currentAverage,
                previousAverage,
                diff,
                reportPeriod.getCurrentLabel(),
                reportPeriod.getPreviousLabel(),
                currentAverage != null,
                previousAverage != null,
                toResponseRange(currentRange),
                toResponseRange(previousRange)
        );
    }

    private LocalDate parseBaseDate(String baseDate) {
        if (baseDate == null || baseDate.isBlank()) {
            return LocalDate.now();
        }

        try {
            return LocalDate.parse(baseDate.trim());
        } catch (DateTimeParseException e) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
    }

    private DateRange calculateCurrentRange(ReportPeriod period, LocalDate baseDate) {
        if (period == ReportPeriod.WEEK) {
            LocalDate startDate = baseDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate endExclusiveDate = startDate.plusWeeks(1);

            return new DateRange(startDate, endExclusiveDate);
        }

        LocalDate startDate = baseDate.withDayOfMonth(1);
        LocalDate endExclusiveDate = startDate.plusMonths(1);

        return new DateRange(startDate, endExclusiveDate);
    }

    private DateRange calculatePreviousRange(ReportPeriod period, DateRange currentRange) {
        if (period == ReportPeriod.WEEK) {
            return new DateRange(
                    currentRange.startDate().minusWeeks(1),
                    currentRange.startDate()
            );
        }

        return new DateRange(
                currentRange.startDate().minusMonths(1),
                currentRange.startDate()
        );
    }

    private BigDecimal findAverage(Long memberId, ReportPracticeType type, DateRange range) {
        LocalDateTime start = range.startDate().atStartOfDay();
        LocalDateTime end = range.endExclusiveDate().atStartOfDay();

        Double average = switch (type) {
            case MY_SENTENCE -> mySentenceAnalysisResultRepository.findAveragePronunciationScore(
                    memberId,
                    start,
                    end
            );
            case BASIC -> basicPronunciationPracticeRepository.findAverageAccuracyScore(
                    memberId,
                    start,
                    end
            );
            case SCENARIO -> scenarioAnalysisResultRepository.findAveragePronunciationScore(
                    memberId,
                    start,
                    end
            );
        };

        if (average == null) {
            return null;
        }

        return BigDecimal.valueOf(average).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateDiff(BigDecimal currentAverage, BigDecimal previousAverage) {
        if (currentAverage == null || previousAverage == null) {
            return null;
        }

        return currentAverage.subtract(previousAverage).setScale(2, RoundingMode.HALF_UP);
    }

    private PronunciationAccuracyReportResDTO.Range toResponseRange(DateRange range) {
        return new PronunciationAccuracyReportResDTO.Range(
                range.startDate(),
                range.endExclusiveDate().minusDays(1)
        );
    }

    private record DateRange(
            LocalDate startDate,
            LocalDate endExclusiveDate
    ) {}
}
