package com.kwcapstone.server.domain.mysentence.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MySentenceCreateReqDTO {
    private String sentenceContent;
}
