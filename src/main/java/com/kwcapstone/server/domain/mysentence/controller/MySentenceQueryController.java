package com.kwcapstone.server.domain.mysentence.controller;

import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceListResDTO;
import com.kwcapstone.server.domain.mysentence.service.MySentenceQueryService;
import com.kwcapstone.server.global.apiPayload.response.ApiResponse;
import com.kwcapstone.server.global.apiPayload.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/warmups/my-sentences")
public class MySentenceQueryController {

    private final MySentenceQueryService mySentenceQueryService;

    @Operation(summary = "저장된 문장 목록 조회")
    @GetMapping
    public ApiResponse<MySentenceListResDTO> getMySentences() {
        MySentenceListResDTO result = mySentenceQueryService.getMySentences();
        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }
}
