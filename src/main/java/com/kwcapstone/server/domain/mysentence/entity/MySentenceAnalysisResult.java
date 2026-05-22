package com.kwcapstone.server.domain.mysentence.entity;

import com.kwcapstone.server.domain.member.entity.Member;
import com.kwcapstone.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(
        name = "my_sentence_analysis_result",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"member_id", "client_request_id"}
                )
        }
)
public class MySentenceAnalysisResult extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "my_sentence_id", nullable = false)
    private MySentence mySentence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "client_request_id", nullable = false)
    private String clientRequestId;

    @Column(name = "user_audio_key", length = 500)
    private String userAudioKey;

    @Column(name = "pronunciation_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal pronunciationScore;

    @Column(name = "speech_rate_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal speechRateScore;

    @Column(name = "silence_ratio", nullable = false, precision = 5, scale = 2)
    private BigDecimal silenceRatio;

    @Column(name = "ai_feedback", columnDefinition = "TEXT")
    private String aiFeedback;

    @Column(name = "syllable_result_json", nullable = false, columnDefinition = "TEXT")
    private String syllableResultJson;
}