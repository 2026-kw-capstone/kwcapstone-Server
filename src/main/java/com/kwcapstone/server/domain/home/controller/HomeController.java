package com.kwcapstone.server.domain.home.controller;

import com.kwcapstone.server.domain.home.dto.response.ContinueLearningResDTO;
import com.kwcapstone.server.domain.home.dto.response.WeeklySummaryResDTO;
import com.kwcapstone.server.domain.home.service.HomeService;
import com.kwcapstone.server.global.apiPayload.response.ApiResponse;
import com.kwcapstone.server.global.apiPayload.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
public class HomeController {
    private final HomeService homeQueryService;

    @Operation(summary = "이어서 학습하기")
    @GetMapping("/continue-learning")
    public ApiResponse<ContinueLearningResDTO> getContinueLearning() {
        ContinueLearningResDTO result = homeQueryService.getContinueLearning();

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    @Operation(summary = "이번 주 요약")
    @GetMapping("/weekly-summary")
    public ApiResponse<WeeklySummaryResDTO> getWeeklySummary() {
        WeeklySummaryResDTO result = homeQueryService.getWeeklySummary();

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }
}
