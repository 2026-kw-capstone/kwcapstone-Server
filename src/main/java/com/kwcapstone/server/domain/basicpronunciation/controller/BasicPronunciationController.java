package com.kwcapstone.server.domain.basicpronunciation.controller;

import com.kwcapstone.server.domain.basicpronunciation.dto.request.BasicPronunciationPracticeReqDTO;
import com.kwcapstone.server.domain.basicpronunciation.dto.response.BasicPronunciationPracticeResDTO;
import com.kwcapstone.server.domain.basicpronunciation.service.BasicPronunciationService;
import com.kwcapstone.server.global.apiPayload.response.ApiResponse;
import com.kwcapstone.server.global.apiPayload.response.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/warmups/basic-pronunciation/practices")
public class BasicPronunciationController {
    private final BasicPronunciationService basicPronunciationService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<BasicPronunciationPracticeResDTO> analyzePractice(
            @ModelAttribute @Valid BasicPronunciationPracticeReqDTO request
    ) {
        BasicPronunciationPracticeResDTO result = basicPronunciationService.analyzePractice(request);

        return ApiResponse.onSuccess(result, SuccessCode.CREATED, request.getClientRequestId());
    }
}
