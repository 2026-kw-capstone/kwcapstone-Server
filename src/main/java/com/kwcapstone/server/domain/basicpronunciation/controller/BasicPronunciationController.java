package com.kwcapstone.server.domain.basicpronunciation.controller;

import com.kwcapstone.server.domain.basicpronunciation.dto.request.BasicPronunciationPracticeReqDTO;
import com.kwcapstone.server.domain.basicpronunciation.dto.response.BasicPronunciationLatestResDTO;
import com.kwcapstone.server.domain.basicpronunciation.dto.response.BasicPronunciationPracticeResDTO;
import com.kwcapstone.server.domain.basicpronunciation.enums.BasicVowel;
import com.kwcapstone.server.domain.basicpronunciation.service.BasicPronunciationService;
import com.kwcapstone.server.global.apiPayload.response.ApiResponse;
import com.kwcapstone.server.global.apiPayload.response.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/latest")
    public ApiResponse<BasicPronunciationLatestResDTO> getLatestPractice(
            @RequestParam BasicVowel targetVowel
    ) {
        BasicPronunciationLatestResDTO result = basicPronunciationService.getLatestPractice(targetVowel);

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }
}
