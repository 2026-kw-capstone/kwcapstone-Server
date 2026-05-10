package com.kwcapstone.server.domain.basicpronunciation.entity;

import com.kwcapstone.server.domain.basicpronunciation.enums.BasicVowel;
import com.kwcapstone.server.domain.member.entity.Member;
import com.kwcapstone.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "basic_pronunciation_practice", uniqueConstraints = {@UniqueConstraint(columnNames = {"member_id", "client_request_id"})})
public class BasicPronunciationPractice extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "client_request_id", nullable = false)
    private String clientRequestId;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_vowel", nullable = false, length = 10)
    private BasicVowel targetVowel;

    @Column(name = "member_audio_key", nullable = false, length = 500)
    private String memberAudioKey;

    @Column(name = "accuracy_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal accuracyScore;

    @Column(name = "feedback", nullable = false, columnDefinition = "TEXT")
    private String feedback;
}
