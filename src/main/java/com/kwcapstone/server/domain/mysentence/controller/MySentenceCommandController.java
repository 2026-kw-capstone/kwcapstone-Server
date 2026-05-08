package com.kwcapstone.server.domain.mysentence.controller;

import com.kwcapstone.server.domain.mysentence.dto.request.MySentenceCreateReqDTO;
import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceCreateResDTO;
import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceDeleteResDTO;
import com.kwcapstone.server.domain.mysentence.service.MySentenceCommandService;
import com.kwcapstone.server.global.apiPayload.response.ApiResponse;
import com.kwcapstone.server.global.apiPayload.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/warmups/my-sentences")
public class MySentenceCommandController {

    private final MySentenceCommandService mySentenceCommandService;

    // 나만의 문장 생성 API
    @Operation(summary = "나만의 문장 생성")
    @PostMapping
    public ApiResponse<MySentenceCreateResDTO> createMySentence(
            @RequestBody @Valid MySentenceCreateReqDTO request
    ) {
        MySentenceCreateResDTO result =
                mySentenceCommandService.createMySentence(request);

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }

    // 나만의 문장 삭제 API
    @Operation(summary = "나만의 문장 삭제")
    @DeleteMapping("/{sentenceId}")
    public ApiResponse<MySentenceDeleteResDTO> deleteMySentence(
            @PathVariable Long sentenceId
    ) {
        MySentenceDeleteResDTO result =
                mySentenceCommandService.deleteMySentence(sentenceId);

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }
}
