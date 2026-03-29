package com.kwcapstone.server.domain.conversation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ConversationListItemResDTO {
    private Long conversationId;
    private String title;
    private LocalDateTime lastMessageAt;
}
