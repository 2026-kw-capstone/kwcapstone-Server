package com.kwcapstone.server.domain.conversation.entity;

import com.kwcapstone.server.domain.conversation.enums.MessageInputType;
import com.kwcapstone.server.domain.conversation.enums.MessageRole;
import com.kwcapstone.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "message")
public class Message extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_free_conversation_id", nullable = false)
    private Conversation conversation;

    @Column(name = "client_request_id", nullable = false, length = 200)
    private String clientRequestId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MessageRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "input_type", nullable = false, length = 20)
    private MessageInputType inputType;

    @Column(name = "message_voice_key", length = 500)
    private String messageVoiceKey;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
}
