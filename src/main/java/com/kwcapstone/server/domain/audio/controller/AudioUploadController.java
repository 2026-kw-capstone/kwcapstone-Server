package com.kwcapstone.server.domain.audio.controller;

import com.kwcapstone.server.domain.audio.dto.request.CompleteAudioUploadReqDTO;
import com.kwcapstone.server.domain.audio.dto.request.PresignedUploadReqDTO;
import com.kwcapstone.server.domain.audio.dto.response.CompleteAudioUploadResDTO;
import com.kwcapstone.server.domain.audio.dto.response.LegacyUploadBenchmarkResDTO;
import com.kwcapstone.server.domain.audio.dto.response.PresignedUploadResDTO;
import com.kwcapstone.server.domain.audio.service.AudioUploadService;
import com.kwcapstone.server.global.apiPayload.response.ApiResponse;
import com.kwcapstone.server.global.apiPayload.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Operation(summary = "S3 음성 파일 직접 업로드 완료 검증")
    @PostMapping("/complete")
    public ResponseEntity<ApiResponse<CompleteAudioUploadResDTO>> completeUpload(
            @RequestBody @Valid CompleteAudioUploadReqDTO request
    ) {
        CompleteAudioUploadResDTO result = audioUploadService.completeUpload(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.onSuccess(result, SuccessCode.OK));
    }

    @Operation(
            summary = "성능 비교용 기존 서버 경유 음성 파일 업로드",
            description = "Presigned PUT 방식과 성능을 비교하기 위한 Benchmark API입니다."
    )
    @PostMapping(
            value = "/benchmark/legacy",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<LegacyUploadBenchmarkResDTO>> uploadLegacyForBenchmark(
            @RequestPart("file") MultipartFile file
    ) {
        LegacyUploadBenchmarkResDTO result = audioUploadService.uploadLegacyForBenchmark(file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(result, SuccessCode.CREATED));
    }
}
