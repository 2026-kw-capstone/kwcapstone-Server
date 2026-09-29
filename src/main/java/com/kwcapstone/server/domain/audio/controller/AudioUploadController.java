package com.kwcapstone.server.domain.audio.controller;

import com.kwcapstone.server.domain.audio.dto.request.PresignedUploadReqDTO;
import com.kwcapstone.server.domain.audio.dto.response.PresignedUploadResDTO;
import com.kwcapstone.server.domain.audio.service.AudioUploadService;
import com.kwcapstone.server.global.apiPayload.response.ApiResponse;
import com.kwcapstone.server.global.apiPayload.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/audio/uploads")
public class AudioUploadController {

    private final AudioUploadService audioUploadService;

    @Operation(summary = "음성 파일 직접 업로드용 Presigned URL 발급")
    @PostMapping("/presigned-url")
    public ResponseEntity<ApiResponse<PresignedUploadResDTO>> generatePresignedUploadUrl(
            @RequestBody @Valid PresignedUploadReqDTO request
    ) {
        PresignedUploadResDTO result = audioUploadService.generatePresignedUploadUrl(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(result, SuccessCode.CREATED));
    }
}
