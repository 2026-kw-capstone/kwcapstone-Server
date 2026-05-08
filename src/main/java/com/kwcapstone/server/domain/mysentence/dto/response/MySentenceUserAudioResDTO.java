package com.kwcapstone.server.domain.mysentence.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MySentenceUserAudioResDTO {
    private Long sentenceId;
    private Long analysisId;
    private String userAudioUrl;
    private Long expiresIn;
}
