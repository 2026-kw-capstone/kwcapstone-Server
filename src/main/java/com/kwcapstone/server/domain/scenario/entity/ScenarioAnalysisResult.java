package com.kwcapstone.server.domain.scenario.entity;

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
        name = "scenario_analysis_result",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"member_id", "client_request_id"}
                )
        }
)
@AttributeOverride(name = "id", column = @Column(name = "scenario_analysis_result_id"))
public class ScenarioAnalysisResult extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_step_id", nullable = false)
    private ScenarioStep scenarioStep;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "client_request_id", nullable = false)
    private String clientRequestId;

    @Column(name = "user_audio_key", length = 500)
    private String userAudioKey;

    @Column(name = "pronunciation_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal pronunciationScore;

    @Column(name = "meaning_delivery_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal meaningDeliveryScore;

    @Column(name = "speech_rate_score", precision = 5, scale = 2)
    private BigDecimal speechRateScore;

    @Column(name = "silence_ratio", precision = 5, scale = 2)
    private BigDecimal silenceRatio;

    @Column(name = "meaning_feedback", columnDefinition = "TEXT")
    private String meaningFeedback;

    @Column(name = "pronunciation_feedback", columnDefinition = "TEXT")
    private String pronunciationFeedback;

    @Column(name = "word_analysis_json", nullable = false, columnDefinition = "TEXT")
    private String wordAnalysisJson;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}