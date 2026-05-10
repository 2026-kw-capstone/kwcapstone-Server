package com.kwcapstone.server.domain.mysentence.controller;

import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceAnalyzeResDTO;
import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceTtsResDTO;
import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceUserAudioResDTO;
import com.kwcapstone.server.domain.mysentence.service.MySentenceAudioService;
import com.kwcapstone.server.global.apiPayload.response.ApiResponse;
import com.kwcapstone.server.global.apiPayload.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/warmups/my-sentences")
public class MySentenceAudioController {

    private final MySentenceAudioService mySentenceAudioService;

    @Operation(summary = "AI TTS 음성 듣기")
    @GetMapping("/{sentenceId}/tts")
    public ApiResponse<MySentenceTtsResDTO> getTts(
            @PathVariable Long sentenceId
    ) {
        MySentenceTtsResDTO result = mySentenceAudioService.getTts(sentenceId);
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
            value = "/{sentenceId}/pronunciations/analyze",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ApiResponse<MySentenceAnalyzeResDTO> analyzePronunciation(
            @PathVariable Long sentenceId,
            @RequestPart("voiceFile") MultipartFile voiceFile
    ) {
        MySentenceAnalyzeResDTO result =
                mySentenceAudioService.analyzePronunciation(sentenceId, voiceFile);

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }
}
