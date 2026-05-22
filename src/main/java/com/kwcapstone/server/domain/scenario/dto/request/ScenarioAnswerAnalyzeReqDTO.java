package com.kwcapstone.server.domain.scenario.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ScenarioAnswerAnalyzeReqDTO {
    @NotBlank
    private String clientRequestId;

    @NotNull
    private Long scenarioId;

    @NotNull
    private Integer level;

    @NotNull
    private Integer stepNo;

    @NotNull
    private MultipartFile voiceFile;
}
