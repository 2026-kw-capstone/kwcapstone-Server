package com.kwcapstone.server.domain.conversation.entity;

import com.kwcapstone.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "message_feedback", uniqueConstraints = {@UniqueConstraint(columnNames = "message_id")})
public class MessageFeedback extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY) // 실제로는 OneToOne
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
}
