package com.kwcapstone.server.domain.conversation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ConversationDetailResDTO {
    private Long conversationId;
    private String title;
    private List<MessageItem> messages;

    @Getter
    @AllArgsConstructor
    public static class MessageItem {
        private String clientRequestId;
        private UserMessage userMessage;
        private AiMessage aiMessage;
        private Feedback feedback;
    }

    @Getter
    @AllArgsConstructor
    public static class UserMessage {
        private Long messageId;
        private String role;
        private String inputType;
        private String voiceUrl;
        private String content;
        private LocalDateTime createdAt;
    }

    @Getter
    @AllArgsConstructor
    public static class AiMessage {
        private Long messageId;
        private String role;
        private String content;
        private LocalDateTime createdAt;
    }

    @Getter
    @AllArgsConstructor
    public static class Feedback {
        private Long feedbackId;
        private String content;
        private LocalDateTime createdAt;
    }
}
