package com.kwcapstone.server.domain.conversation.client.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FreeTalkAiResDTO {
    private Boolean success;
    private String aiReply;
    private String aiFeedback;
    private String assistantMessageForHistory;
}
