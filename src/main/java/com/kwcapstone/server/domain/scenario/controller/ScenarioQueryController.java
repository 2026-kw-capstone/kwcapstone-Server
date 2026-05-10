package com.kwcapstone.server.domain.scenario.controller;

import com.kwcapstone.server.domain.scenario.dto.response.ScenarioListResDTO;
import com.kwcapstone.server.domain.scenario.service.ScenarioQueryService;
import com.kwcapstone.server.global.apiPayload.response.ApiResponse;
import com.kwcapstone.server.global.apiPayload.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/conversations/scenarios")
public class ScenarioQueryController {

    private final ScenarioQueryService scenarioQueryService;

    @Operation(summary = "시나리오 목록 조회")
    @GetMapping
    public ApiResponse<ScenarioListResDTO> getScenarioList() {
        ScenarioListResDTO result = scenarioQueryService.getScenarioList();

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }
}