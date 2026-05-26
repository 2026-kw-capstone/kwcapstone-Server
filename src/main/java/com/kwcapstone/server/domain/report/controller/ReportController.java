package com.kwcapstone.server.domain.report.controller;

import com.kwcapstone.server.domain.report.dto.response.PronunciationAccuracyReportResDTO;
import com.kwcapstone.server.domain.report.dto.response.WeeklyStampsReportResDTO;
import com.kwcapstone.server.domain.report.service.ReportService;
import com.kwcapstone.server.global.apiPayload.response.ApiResponse;
import com.kwcapstone.server.global.apiPayload.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reportService;

    @Operation(summary = "주/월 평균 발음 정확도 조회")
    @GetMapping("/pronunciation-accuracy")
    public ApiResponse<PronunciationAccuracyReportResDTO> getPronunciationAccuracy(
            @RequestParam String period,
            @RequestParam String type,
            @RequestParam(required = false) String baseDate
    ) {
        PronunciationAccuracyReportResDTO result = reportService.getPronunciationAccuracy(period, type, baseDate);

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    @Operation(summary = "위클리 스탬프 조회")
    @GetMapping("/weekly-stamps")
    public ApiResponse<WeeklyStampsReportResDTO> getWeeklyStamps(
            @RequestParam(required = false) String baseDate
    ) {
        WeeklyStampsReportResDTO result = reportService.getWeeklyStamps(baseDate);

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }
}
