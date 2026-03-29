package com.kwcapstone.server.domain.conversation.service;

import com.kwcapstone.server.domain.conversation.converter.ConversationConverter;
import com.kwcapstone.server.domain.conversation.dto.request.ConversationTitleUpdateReqDTO;
import com.kwcapstone.server.domain.conversation.dto.response.ConversationTitleUpdateResDTO;
import com.kwcapstone.server.domain.conversation.entity.Conversation;
import com.kwcapstone.server.domain.conversation.exception.code.ConversationErrorCode;
import com.kwcapstone.server.domain.conversation.repository.ConversationRepository;
import com.kwcapstone.server.domain.conversation.repository.MessageFeedbackRepository;
import com.kwcapstone.server.domain.conversation.repository.MessageRepository;
import com.kwcapstone.server.global.apiPayload.exception.CustomException;
import com.kwcapstone.server.global.security.SecurityUtil;
import com.kwcapstone.server.global.storage.audio.AudioStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ConversationCommandServiceImpl implements ConversationCommandService {
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final MessageFeedbackRepository messageFeedbackRepository;
    private final AudioStorageService audioStorageService;

    @Override
    public ConversationTitleUpdateResDTO updateConversationTitle(Long conversationId, ConversationTitleUpdateReqDTO request) {
        Long memberId = SecurityUtil.getCurrentMemberId();

        Conversation conversation = getOwnedConversation(memberId, conversationId);

        conversation.updateTitle(request.getTitle().trim());

        return ConversationConverter.toConversationTitleUpdateResDTO(conversation);
    }

    // 삭제 순서: S3 삭제 -> DB 삭제
    @Override
    public void deleteConversation(Long conversationId) {
        Long memberId = SecurityUtil.getCurrentMemberId();

        validateOwnedConversation(memberId, conversationId);

        // Message Entity 자체 조회가 아닌, voice key만 조회
        List<String> voiceKeys = messageRepository.findDistinctVoiceKeysByConversationId(conversationId);

        // S3 음성 파일 삭제
        audioStorageService.deleteAll(voiceKeys);

        // FK 순서 고려: feedback -> message -> conversation
        messageFeedbackRepository.deleteAllByConversationId(conversationId);
        messageRepository.deleteAllByConversationId(conversationId);
        conversationRepository.deleteById(conversationId);
    }

    private Conversation getOwnedConversation(Long memberId, Long conversationId) {
        return conversationRepository.findByIdAndMemberId(conversationId, memberId)
                .orElseThrow(() -> {
                    boolean exists = conversationRepository.existsById(conversationId);

                    return exists
                            ? new CustomException(ConversationErrorCode.CONVERSATION_FORBIDDEN)
                            : new CustomException(ConversationErrorCode.CONVERSATION_NOT_FOUND);
                });
    }

    private void validateOwnedConversation(Long memberId, Long conversationId) {
        boolean owned = conversationRepository.findByIdAndMemberId(conversationId, memberId).isPresent();

        if (!owned) {
            boolean exists = conversationRepository.existsById(conversationId);

            throw exists
                    ? new CustomException(ConversationErrorCode.CONVERSATION_FORBIDDEN)
                    : new CustomException(ConversationErrorCode.CONVERSATION_NOT_FOUND);
        }
    }
}
