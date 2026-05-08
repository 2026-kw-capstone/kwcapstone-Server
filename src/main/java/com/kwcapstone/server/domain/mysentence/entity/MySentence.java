package com.kwcapstone.server.domain.mysentence.entity;

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
@Table(name = "my_sentence")
public class MySentence extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "sentence_content", nullable = false, length = 300)
    private String sentenceContent;

    @Column(name = "ai_audio_key", length = 500)
    private String aiAudioKey;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void updateAiAudioKey(String aiAudioKey) {
        this.aiAudioKey = aiAudioKey;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}
