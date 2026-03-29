package com.kwcapstone.server.domain.conversation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TextMessageSendReqDTO {
    private Long conversationId;

    @NotBlank
    private String clientRequestId;

    @NotBlank
    private String content;
}
