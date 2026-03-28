package com.kwcapstone.server.domain.conversation.converter;

import com.kwcapstone.server.domain.conversation.dto.response.ConversationDetailResDTO;
import com.kwcapstone.server.domain.conversation.dto.response.ConversationListItemResDTO;
import com.kwcapstone.server.domain.conversation.dto.response.MessageSendResDTO;
import com.kwcapstone.server.domain.conversation.entity.Conversation;
import com.kwcapstone.server.domain.conversation.entity.Message;
import com.kwcapstone.server.domain.conversation.entity.MessageFeedback;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConversationConverter {
    // 엔티티 -> 메시지 전송 응답 DTO
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

    // 엔티티 -> 대화 목록 조회 응답 DTO
    public static ConversationListItemResDTO toConversationListItemResDTO(Conversation conversation) {
        return new ConversationListItemResDTO(
                conversation.getId(),
                conversation.getTitle(),
                conversation.getLastMessageAt()
        );
    }

    // 엔티티 + 메시지 목록 -> 대화 상세 조회 응답 DTO
    public static ConversationDetailResDTO toConversationDetailResDTO(
            Conversation conversation,
            List<ConversationDetailResDTO.MessageItem> messages
    ) {
        return new ConversationDetailResDTO(
                conversation.getId(),
                conversation.getTitle(),
                messages
        );
    }

    // user, ai, feedback 묶음 -> 상세 조회 messages 항목 DTO
    public static ConversationDetailResDTO.MessageItem toConversationDetailMessageItem(
            Message userMessage,
            Message aiMessage,
            MessageFeedback feedback,
            String voiceUrl
    ) {
        return new ConversationDetailResDTO.MessageItem(
                userMessage.getClientRequestId(),
                new ConversationDetailResDTO.UserMessage(
                        userMessage.getId(),
                        userMessage.getRole().name(),
                        userMessage.getInputType().name(),
                        voiceUrl,
                        userMessage.getContent(),
                        userMessage.getCreatedAt()
                ),
                new ConversationDetailResDTO.AiMessage(
                        aiMessage.getId(),
                        aiMessage.getRole().name(),
                        aiMessage.getContent(),
                        aiMessage.getCreatedAt()
                ),
                new ConversationDetailResDTO.Feedback(
                        feedback.getId(),
                        feedback.getContent(),
                        feedback.getCreatedAt()
                )
        );
    }
}
