package com.kwcapstone.server.domain.conversation.entity;

import com.kwcapstone.server.domain.member.entity.Member;
import com.kwcapstone.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "ai_free_conversation")
public class Conversation extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "last_message_at", nullable = false)
    private LocalDateTime lastMessageAt;

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateLastMessageAt(LocalDateTime lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }
}
