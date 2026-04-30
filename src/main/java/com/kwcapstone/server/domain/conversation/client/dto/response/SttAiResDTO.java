package com.kwcapstone.server.domain.conversation.client.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SttAiResDTO {
    private Boolean success;
    private String sttText;
}
