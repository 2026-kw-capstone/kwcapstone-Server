package com.kwcapstone.server.domain.conversation.service;

import com.kwcapstone.server.domain.conversation.converter.ConversationConverter;
import com.kwcapstone.server.domain.conversation.dto.response.ConversationDetailResDTO;
import com.kwcapstone.server.domain.conversation.dto.response.ConversationListItemResDTO;
import com.kwcapstone.server.domain.conversation.entity.Conversation;
import com.kwcapstone.server.domain.conversation.entity.Message;
import com.kwcapstone.server.domain.conversation.entity.MessageFeedback;
import com.kwcapstone.server.domain.conversation.enums.MessageRole;
import com.kwcapstone.server.domain.conversation.exception.code.ConversationErrorCode;
import com.kwcapstone.server.domain.conversation.repository.ConversationRepository;
import com.kwcapstone.server.domain.conversation.repository.MessageFeedbackRepository;
import com.kwcapstone.server.domain.conversation.repository.MessageRepository;
import com.kwcapstone.server.global.apiPayload.exception.CustomException;
import com.kwcapstone.server.global.apiPayload.response.ErrorCode;
import com.kwcapstone.server.global.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConversationQueryServiceImpl implements ConversationQueryService {
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final MessageFeedbackRepository messageFeedbackRepository;

    @Override
    public List<ConversationListItemResDTO> getConversations() {
        Long memberId = SecurityUtil.getCurrentMemberId();

        return conversationRepository.findAllByMemberIdOrderByLastMessageAtDesc(memberId).stream()
                .map(ConversationConverter::toConversationListItemResDTO)
                .toList();
    }

    @Override
    public ConversationDetailResDTO getConversationDetail(Long conversationId) {
        Long memberId = SecurityUtil.getCurrentMemberId();

        Conversation conversation = getOwnedConversation(memberId, conversationId);
        List<Message> messages = messageRepository.findAllByConversationIdOrderByCreatedAtAscIdAsc(conversationId);
        List<MessageFeedback> feedbacks = messageFeedbackRepository.findAllByMessageConversationId(conversationId);

        // 피드백
        Map<Long, MessageFeedback> feedbackMap = new LinkedHashMap<>();

        for (MessageFeedback feedback : feedbacks) {
            feedbackMap.put(feedback.getMessage().getId(), feedback);
        }

        // 메시지
        // clientRequestId를 요청 단위로 묶어서 user, ai 메시지를 한 쌍으로 그룹화
        Map<String, MessageGroup> grouped = new LinkedHashMap<>(); // 입력 순서 유지

        for (Message message : messages) {
            MessageGroup group = grouped.computeIfAbsent(
                    message.getClientRequestId(),
                    clientRequestId -> new MessageGroup()
            );

            if (message.getRole() == MessageRole.USER) {
                group.userMessage = message;
            } else if (message.getRole() == MessageRole.AI) {
                group.aiMessage = message;
            }
        }

        List<ConversationDetailResDTO.MessageItem> messageItems = new ArrayList<>();

        for (Map.Entry<String, MessageGroup> entry : grouped.entrySet()) {
            MessageGroup group = entry.getValue();

            if (group.userMessage == null || group.aiMessage == null) {
                throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
            }

            MessageFeedback feedback = feedbackMap.get(group.userMessage.getId());

            if (feedback == null) {
                throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
            }

            messageItems.add(
                    ConversationConverter.toConversationDetailMessageItem(
                            group.userMessage,
                            group.aiMessage,
                            feedback,
                            resolveVoiceUrl(group.userMessage)
                    )
            );
        }

        return ConversationConverter.toConversationDetailResDTO(conversation, messageItems);
    }

    private Conversation getOwnedConversation(Long memberId, Long conversationId) {
        return conversationRepository.findByIdAndMemberId(conversationId, memberId)
                .orElseThrow(() -> {
                    boolean exists = conversationRepository.existsById(conversationId);

                    return exists
                            ? new CustomException(ConversationErrorCode.CONVERSATION_FORBIDDEN) // 403
                            : new CustomException(ConversationErrorCode.CONVERSATION_NOT_FOUND); // 404
                });
    }

    // 현재는 음성 재생 기능을 AI 자유대화에 붙이지 않았으므로 null 반환
    // 이후 재생 기능이 필요해지면 presigned URL 생성 로직으로 교체
    private String resolveVoiceUrl(Message userMessage) {
        return null;
    }

    private static class MessageGroup {
        private Message userMessage;
        private Message aiMessage;
    }
}
