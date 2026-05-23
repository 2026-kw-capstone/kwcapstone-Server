package com.kwcapstone.server.domain.mysentence.client.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MySentenceTtsAiResDTO {
    private Long sentenceId;
    private String sentenceContent;
    private String aiAudioUrl;
    private Long expiresIn;
}
