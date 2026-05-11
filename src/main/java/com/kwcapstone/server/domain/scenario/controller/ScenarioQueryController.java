package com.kwcapstone.server.domain.scenario.controller;

import com.kwcapstone.server.domain.scenario.dto.response.ScenarioDetailResDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioLevelResultResDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioListResDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioStepDetailResDTO;
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

    @Operation(summary = "시나리오 상세 및 레벨 목록 조회")
    @GetMapping("/{scenarioId}")
    public ApiResponse<ScenarioDetailResDTO> getScenarioDetail(
            @PathVariable Long scenarioId
    ) {
        ScenarioDetailResDTO result = scenarioQueryService.getScenarioDetail(scenarioId);

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    @Operation(summary = "대화 단계 조회")
    @GetMapping("/{scenarioId}/levels/{level}/steps/{stepNo}")
    public ApiResponse<ScenarioStepDetailResDTO> getScenarioStep(
            @PathVariable Long scenarioId,
            @PathVariable Integer level,
            @PathVariable Integer stepNo
    ) {
        ScenarioStepDetailResDTO result =
                scenarioQueryService.getScenarioStep(scenarioId, level, stepNo);

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    @Operation(summary = "시나리오 훈련 완료 결과 조회")
    @GetMapping("/{scenarioId}/levels/{level}/result")
    public ApiResponse<ScenarioLevelResultResDTO> getScenarioLevelResult(
            @PathVariable Long scenarioId,
            @PathVariable Integer level
    ) {
        ScenarioLevelResultResDTO result =
                scenarioQueryService.getScenarioLevelResult(scenarioId, level);

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }
}