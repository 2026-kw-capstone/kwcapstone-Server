package com.kwcapstone.server.domain.conversation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConversationTitleUpdateResDTO {
    private Long conversationId;
    private String title;
}
