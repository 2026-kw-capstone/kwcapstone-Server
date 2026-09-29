package com.kwcapstone.server.domain.audio.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompleteAudioUploadReqDTO {
    @NotBlank
    private String key;
}
