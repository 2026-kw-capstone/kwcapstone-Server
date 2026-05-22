package com.kwcapstone.server.domain.mysentence.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class MySentenceAnalyzeReqDTO {
    @NotBlank
    private String clientRequestId;

    @NotNull
    private Long sentenceId;

    @NotNull
    private MultipartFile voiceFile;
}