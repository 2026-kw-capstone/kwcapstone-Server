package com.kwcapstone.server.domain.scenario.controller;

import com.kwcapstone.server.domain.scenario.dto.response.ScenarioAnswerAnalyzeResDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioUserAudioResDTO;
import com.kwcapstone.server.domain.scenario.service.ScenarioAudioService;
import com.kwcapstone.server.global.apiPayload.response.ApiResponse;
import com.kwcapstone.server.global.apiPayload.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/conversations/scenarios")
public class ScenarioAudioController {

    private final ScenarioAudioService scenarioAudioService;

    @Operation(summary = "녹음 파일 업로드 및 시나리오 음성 답변 분석")
    @PostMapping(
            value = "/{scenarioId}/levels/{level}/steps/{stepNo}/answers",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ApiResponse<ScenarioAnswerAnalyzeResDTO> analyzeScenarioAnswer(
            @PathVariable Long scenarioId,
            @PathVariable Integer level,
            @PathVariable Integer stepNo,
            @RequestPart("voiceFile") MultipartFile voiceFile
    ) {
        ScenarioAnswerAnalyzeResDTO result =
                scenarioAudioService.analyzeScenarioAnswer(
                        scenarioId,
                        level,
                        stepNo,
                        voiceFile
                );

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