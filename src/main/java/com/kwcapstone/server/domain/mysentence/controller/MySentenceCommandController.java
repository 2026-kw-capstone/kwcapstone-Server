package com.kwcapstone.server.domain.mysentence.controller;

import com.kwcapstone.server.domain.mysentence.dto.request.MySentenceCreateReqDTO;
import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceCreateResDTO;
import com.kwcapstone.server.domain.mysentence.service.MySentenceCommandService;
import com.kwcapstone.server.global.apiPayload.response.ApiResponse;
import com.kwcapstone.server.global.apiPayload.response.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/warmups/my-sentences")
public class MySentenceCommandController {

    private final MySentenceCommandService mySentenceCommandService;

    // 나만의 문장 생성 API
    @PostMapping
    public ApiResponse<MySentenceCreateResDTO> createMySentence(
            @RequestBody @Valid MySentenceCreateReqDTO request
    ) {
        MySentenceCreateResDTO result =
                mySentenceCommandService.createMySentence(request);

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }
}
