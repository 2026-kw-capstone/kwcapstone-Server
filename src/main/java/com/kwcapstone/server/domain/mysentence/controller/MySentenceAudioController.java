package com.kwcapstone.server.domain.mysentence.controller;

import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceTtsResDTO;
import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceUserAudioResDTO;
import com.kwcapstone.server.domain.mysentence.service.MySentenceAudioService;
import com.kwcapstone.server.global.apiPayload.response.ApiResponse;
import com.kwcapstone.server.global.apiPayload.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
