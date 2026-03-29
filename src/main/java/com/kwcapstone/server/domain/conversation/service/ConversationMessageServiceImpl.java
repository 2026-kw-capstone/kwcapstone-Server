package com.kwcapstone.server.domain.conversation.service;

import com.kwcapstone.server.domain.conversation.converter.ConversationConverter;
import com.kwcapstone.server.domain.conversation.dto.request.TextMessageSendReqDTO;
import com.kwcapstone.server.domain.conversation.dto.request.VoiceMessageSendReqDTO;
import com.kwcapstone.server.domain.conversation.dto.response.MessageSendResDTO;
import com.kwcapstone.server.domain.conversation.entity.Conversation;
import com.kwcapstone.server.domain.conversation.entity.Message;
import com.kwcapstone.server.domain.conversation.entity.MessageFeedback;
import com.kwcapstone.server.domain.conversation.enums.MessageInputType;
import com.kwcapstone.server.domain.conversation.enums.MessageRole;
import com.kwcapstone.server.domain.conversation.exception.code.ConversationErrorCode;
import com.kwcapstone.server.domain.conversation.repository.ConversationRepository;
import com.kwcapstone.server.domain.conversation.repository.MessageFeedbackRepository;
import com.kwcapstone.server.domain.conversation.repository.MessageRepository;
import com.kwcapstone.server.domain.member.entity.Member;
import com.kwcapstone.server.domain.member.exception.code.MemberErrorCode;
import com.kwcapstone.server.domain.member.repository.MemberRepository;
import com.kwcapstone.server.global.apiPayload.exception.CustomException;
import com.kwcapstone.server.global.apiPayload.response.ErrorCode;
import com.kwcapstone.server.global.security.SecurityUtil;
import com.kwcapstone.server.global.storage.audio.AudioStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ConversationMessageServiceImpl implements ConversationMessageService {
    private static final String FREE_CONVERSATION_AUDIO_PREFIX = "free-conversation";

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final MessageFeedbackRepository messageFeedbackRepository;
    private final MemberRepository memberRepository;
    private final AudioStorageService audioStorageService;

    // 텍스트 메시지 전송 API 로직
    @Override
    public MessageSendResDTO sendTextMessage(TextMessageSendReqDTO request) {
        // 현재 로그인한 사용자의 ID 조회
        Long memberId = SecurityUtil.getCurrentMemberId();

        // 중복 요청 체크(멱등성 처리)
        MessageSendResDTO duplicated = findDuplicatedResponse(memberId, request.getClientRequestId());

        // 이미 처리된 요청이면 기존 응답 그대로 반환
        if (duplicated != null) {
            return duplicated;
        }

        // 메시지 내용
        String userContent = request.getContent().trim();

        /**
         * 채팅방 생성 또는 조회
         * conversationId 없음 -> 새 대화(채팅방) 생성
         * conversationId 있음 -> 기존 대화 조회
         */
        Conversation conversation = getOrCreateConversation(
                memberId,
                request.getConversationId(),
                generateTitleFromText(userContent)
        );

        // USER 메시지 저장
        Message userMessage = messageRepository.save(
                Message.builder()
                        .conversation(conversation)
                        .clientRequestId(request.getClientRequestId())
                        .role(MessageRole.USER)
                        .inputType(MessageInputType.TEXT)
                        .messageVoiceKey(null)
                        .content(userContent)
                        .build()
        );

        // TODO: FastAPI 연동 후 AI 서버 응답으로 대체
        // ~

        String aiResponse = "임시 AI 응답";
        String feedbackContent = "임시 피드백";

        // AI 메시지 생성
        Message aiMessage = messageRepository.save(
                Message.builder()
                        .conversation(conversation)
                        .clientRequestId(request.getClientRequestId())
                        .role(MessageRole.AI)
                        .inputType(MessageInputType.TEXT)
                        .messageVoiceKey(null)
                        .content(aiResponse)
                        .build()
        );

        // 피드백 생성
        MessageFeedback feedback = messageFeedbackRepository.save(
                MessageFeedback.builder()
                        .message(userMessage)
                        .content(feedbackContent)
                        .build()
        );

        conversation.updateLastMessageAt(LocalDateTime.now());

        return ConversationConverter.toMessageSendResDTO(
                conversation,
                userMessage,
                aiMessage,
                feedback,
                null // TODO: 리팩토링 대상
        );
    }

    // 음성 메시지 전송 API 로직
    @Override
    public MessageSendResDTO sendVoiceMessage(VoiceMessageSendReqDTO request) {
        Long memberId = SecurityUtil.getCurrentMemberId();

        MessageSendResDTO duplicated = findDuplicatedResponse(memberId, request.getClientRequestId());

        if (duplicated != null) {
            return duplicated;
        }

        // S3 업로드
        String voiceKey = audioStorageService.upload(
                buildConversationAudioKeyPrefix(memberId),
                request.getClientRequestId(),
                request.getVoiceFile()
        );

        // TODO: FastAPI 연동 시 여기서 presigned URL 생성 후 AI 서버로 전달
        // String presignedUrl = s3Uploader.generatePresignedGetUrl(voiceKey);
        // ~

        String transcript = "임시 STT 결과"; // STT 결과
        String aiResponse = "임시 AI 응답";
        String feedbackContent = "임시 피드백";

        Conversation conversation = getOrCreateConversation(
                memberId,
                request.getConversationId(),
                generateTitleFromText(transcript)
        );

        Message userMessage = messageRepository.save(
                Message.builder()
                        .conversation(conversation)
                        .clientRequestId(request.getClientRequestId())
                        .role(MessageRole.USER)
                        .inputType(MessageInputType.VOICE)
                        .messageVoiceKey(voiceKey)
                        .content(transcript)
                        .build()
        );

        Message aiMessage = messageRepository.save(
                Message.builder()
                        .conversation(conversation)
                        .clientRequestId(request.getClientRequestId())
                        .role(MessageRole.AI)
                        .inputType(MessageInputType.TEXT)
                        .messageVoiceKey(null)
                        .content(aiResponse)
                        .build()
        );

        MessageFeedback feedback = messageFeedbackRepository.save(
                MessageFeedback.builder()
                        .message(userMessage)
                        .content(feedbackContent)
                        .build()
        );

        conversation.updateLastMessageAt(LocalDateTime.now());

        return ConversationConverter.toMessageSendResDTO(
                conversation,
                userMessage,
                aiMessage,
                feedback,
                null // TODO: 리팩토링 대상
        );
    }

    /**
     * 1. USER 메시지 조회
     * 2. AI 메시지 조회
     * 3. 피드백 조회
     * 4. 기존 응답 반환
     */
    private MessageSendResDTO findDuplicatedResponse(Long memberId, String clientRequestId) {
        Message userMessage = messageRepository.findByMemberIdAndClientRequestIdAndRole(
                memberId,
                clientRequestId,
                MessageRole.USER
        )
        .orElse(null);

        if (userMessage == null) {
            return null;
        }

        Long conversationId = userMessage.getConversation().getId();

        Message aiMessage = messageRepository.findByConversationIdAndClientRequestIdAndRole(
                conversationId,
                clientRequestId,
                MessageRole.AI
        )
        .orElseThrow(() -> new CustomException(ErrorCode.INTERNAL_SERVER_ERROR));

        MessageFeedback feedback = messageFeedbackRepository.findByMessageId(userMessage.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.INTERNAL_SERVER_ERROR));

        return ConversationConverter.toMessageSendResDTO(
                userMessage.getConversation(),
                userMessage,
                aiMessage,
                feedback,
                null
        );
    }

    private Conversation getOrCreateConversation(Long memberId, Long conversationId, String title) {
        // 새 대화(채팅방) 생성
        if (conversationId == null) {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

            Conversation conversation = Conversation.builder()
                    .member(member)
                    .title(title)
                    .lastMessageAt(LocalDateTime.now())
                    .build();

            return conversationRepository.save(conversation);
        }

        // 기존 대화(채팅방) 조회
        return conversationRepository.findByIdAndMemberId(conversationId, memberId)
                .orElseThrow(() -> {
                    boolean exists = conversationRepository.existsById(conversationId); // 해당 채팅방이 DB에 존재하는지

                    return exists
                            ? new CustomException(ConversationErrorCode.CONVERSATION_FORBIDDEN)
                            : new CustomException(ConversationErrorCode.CONVERSATION_NOT_FOUND);
                });
    }

    // 채팅방 제목 생성 메서드
    private String generateTitleFromText(String content) {
        String normalized = content.replaceAll("\\s+", " ").trim(); // 공백 정리

        if (!StringUtils.hasText(normalized)) {
            return "새 대화";
        }

        int maxLength = 20; // 채팅방 제목 길이 제한

        return normalized.length() <= maxLength
                ? normalized
                : normalized.substring(0, maxLength);
    }

    private String buildConversationAudioKeyPrefix(Long memberId) {
        return FREE_CONVERSATION_AUDIO_PREFIX + "/" + memberId;
    }
}
