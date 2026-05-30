package com.kwcapstone.server.domain.scenario.controller;

import com.kwcapstone.server.domain.scenario.dto.request.ScenarioCreateReqDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioCreateResDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioDeleteResDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioRegenerateResDTO;
import com.kwcapstone.server.domain.scenario.service.ScenarioCommandService;
import com.kwcapstone.server.global.apiPayload.response.ApiResponse;
import com.kwcapstone.server.global.apiPayload.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/conversations/scenarios")
public class ScenarioCommandController {

    private final ScenarioCommandService scenarioCommandService;

    @Operation(summary = "시나리오 생성")
    @PostMapping
    public ApiResponse<ScenarioCreateResDTO> createScenario(
            @RequestBody ScenarioCreateReqDTO request
    ) {
        ScenarioCreateResDTO result = scenarioCommandService.createScenario(request);

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    @Operation(summary = "시나리오 단계 재생성")
    @PostMapping("/{scenarioId}/levels/{level}/steps/{stepNo}/regenerate")
    public ApiResponse<ScenarioRegenerateResDTO> regenerateScenarioStep(
            @PathVariable Long scenarioId,
            @PathVariable Integer level,
            @PathVariable Integer stepNo
    ) {
        ScenarioRegenerateResDTO result =
                scenarioCommandService.regenerateScenarioStep(scenarioId, level, stepNo);

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    @Operation(summary = "시나리오 삭제")
    @DeleteMapping("/{scenarioId}")
    public ApiResponse<ScenarioDeleteResDTO> deleteScenario(
            @PathVariable Long scenarioId
    ) {
        ScenarioDeleteResDTO result =
                scenarioCommandService.deleteScenario(scenarioId);

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }
}