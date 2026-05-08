package com.kwcapstone.server.domain.mysentence.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MySentenceDetailResDTO {
    private Long sentenceId;
    private String sentenceContent;
}
