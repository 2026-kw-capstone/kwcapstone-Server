package com.kwcapstone.server.domain.scenario.controller;

import com.kwcapstone.server.domain.scenario.dto.request.ScenarioAnswerAnalyzeReqDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioAnswerAnalyzeResDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioUserAudioResDTO;
import com.kwcapstone.server.domain.scenario.service.ScenarioAudioService;
import com.kwcapstone.server.global.apiPayload.response.ApiResponse;
import com.kwcapstone.server.global.apiPayload.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/conversations/scenarios")
public class ScenarioAudioController {

    private final ScenarioAudioService scenarioAudioService;

    @Operation(summary = "녹음 파일 업로드 및 음성 분석")
    @PostMapping(
            value = "/answers",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ApiResponse<ScenarioAnswerAnalyzeResDTO> analyzeScenarioAnswer(
            @Valid @ModelAttribute ScenarioAnswerAnalyzeReqDTO request
    ) {
        ScenarioAnswerAnalyzeResDTO result =
                scenarioAudioService.analyzeScenarioAnswer(request);

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    @Operation(summary = "시나리오 내 음성 듣기")
    @GetMapping("/{scenarioId}/levels/{level}/steps/{stepNo}/user-audio")
    public ApiResponse<ScenarioUserAudioResDTO> getScenarioUserAudio(
            @PathVariable Long scenarioId,
            @PathVariable Integer level,
            @PathVariable Integer stepNo
    ) {
        ScenarioUserAudioResDTO result =
                scenarioAudioService.getScenarioUserAudio(
                        scenarioId,
                        level,
                        stepNo
                );

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }
}