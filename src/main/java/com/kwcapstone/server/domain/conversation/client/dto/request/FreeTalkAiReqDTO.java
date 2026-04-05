package com.kwcapstone.server.domain.conversation.client.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FreeTalkAiReqDTO {
    private String userMessage;
    private List<ChatHistoryItem> chatHistory;

    @Getter
    @AllArgsConstructor
    public static class ChatHistoryItem {
        private String role; // user | assistant
        private String content;
    }
}
