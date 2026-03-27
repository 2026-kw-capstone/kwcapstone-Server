package com.kwcapstone.server.domain.conversation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessageSendResDTO {
    private Long conversationId;
    private UserMessage userMessage;
    private AiMessage aiMessage;
    private Feedback feedback;

    @Getter
    @AllArgsConstructor
    public static class UserMessage {
        private Long messageId;
        private String role;
        private String inputType;
        private String voiceUrl;
        private String content;
    }

    @Getter
    @AllArgsConstructor
    public static class AiMessage {
        private Long messageId;
        private String role;
        private String content;
    }

    @Getter
    @AllArgsConstructor
    public static class Feedback {
        private Long feedbackId;
        private String content;
    }
}
