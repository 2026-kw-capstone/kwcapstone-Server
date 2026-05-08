package com.kwcapstone.server.domain.mysentence.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MySentenceTtsResDTO {
    private Long sentenceId;
    private String sentenceContent;
    private String aiAudioUrl;
    private Long expiresIn;
}
