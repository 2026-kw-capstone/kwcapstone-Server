package com.kwcapstone.server.domain.conversation.converter;

import com.kwcapstone.server.domain.conversation.dto.response.MessageSendResDTO;
import com.kwcapstone.server.domain.conversation.entity.Conversation;
import com.kwcapstone.server.domain.conversation.entity.Message;
import com.kwcapstone.server.domain.conversation.entity.MessageFeedback;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConversationConverter {
    // 엔티티 -> 응답 DTO
    public static MessageSendResDTO toMessageSendResDTO(
            Conversation conversation,
            Message userMessage,
            Message aiMessage,
            MessageFeedback feedback,
            String voiceUrl
    ) {
        return new MessageSendResDTO(
                conversation.getId(),
                new MessageSendResDTO.UserMessage(
                        userMessage.getId(),
                        userMessage.getRole().name(),
                        userMessage.getInputType().name(),
                        voiceUrl,
                        userMessage.getContent()
                ),
                new MessageSendResDTO.AiMessage(
                        aiMessage.getId(),
                        aiMessage.getRole().name(),
                        aiMessage.getContent()
                ),
                new MessageSendResDTO.Feedback(
                        feedback.getId(),
                        feedback.getContent()
                )
        );
    }
}
