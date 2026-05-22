package com.kwcapstone.server.domain.mysentence.controller;

import com.kwcapstone.server.domain.mysentence.dto.request.MySentenceAnalyzeReqDTO;
import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceAnalyzeResDTO;
import com.kwcapstone.server.domain.mysentence.client.dto.response.MySentenceTtsAiResDTO;
import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceUserAudioResDTO;
import com.kwcapstone.server.domain.mysentence.service.MySentenceAudioService;
import com.kwcapstone.server.global.apiPayload.response.ApiResponse;
import com.kwcapstone.server.global.apiPayload.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/warmups/my-sentences")
public class MySentenceAudioController {

    private final MySentenceAudioService mySentenceAudioService;

    @Operation(summary = "AI TTS 음성 듣기")
    @GetMapping("/{sentenceId}/tts")
    public ApiResponse<MySentenceTtsAiResDTO> getTts(
            @PathVariable Long sentenceId
    ) {
        MySentenceTtsAiResDTO result = mySentenceAudioService.getTts(sentenceId);
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    @Operation(summary = "내 음성 듣기")
    @GetMapping("/{sentenceId}/user-audio")
    public ApiResponse<MySentenceUserAudioResDTO> getUserAudio(
            @PathVariable Long sentenceId
    ) {
        MySentenceUserAudioResDTO result =
                mySentenceAudioService.getUserAudio(sentenceId);

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    @Operation(summary = " 녹음 파일 업로드 및 발음 분석")
    @PostMapping(
            value = "/pronunciations/analyze",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ApiResponse<MySentenceAnalyzeResDTO> analyzePronunciation(
            @Valid @ModelAttribute MySentenceAnalyzeReqDTO request
    ) {
        MySentenceAnalyzeResDTO result =
                mySentenceAudioService.analyzePronunciation(request);

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }
}
