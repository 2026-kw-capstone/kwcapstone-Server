package com.kwcapstone.server.domain.audio.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PresignedUploadReqDTO {
    @NotBlank
    private String fileName;

    @NotBlank
    private String contentType;

    @Positive
    private long fileSize; // byte 단위
}
