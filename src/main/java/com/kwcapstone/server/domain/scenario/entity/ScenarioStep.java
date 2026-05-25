package com.kwcapstone.server.domain.scenario.entity;

import com.kwcapstone.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "scenario_step")
@AttributeOverride(name = "id", column = @Column(name = "scenario_step_id"))
public class ScenarioStep extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_level_id", nullable = false)
    private ScenarioLevel scenarioLevel;

    @Column(name = "step_no", nullable = false)
    private Integer stepNo;

    @Column(name = "step_name", nullable = false, length = 60)
    private String stepName;

    @Column(name = "assistant_message", nullable = false, length = 255)
    private String assistantMessage;

    @Column(name = "user_intent", nullable = false, length = 255)
    private String userIntent;

    public void setScenarioLevel(ScenarioLevel scenarioLevel) {
        this.scenarioLevel = scenarioLevel;
    }

    public void updateContent(
            String stepName,
            String assistantMessage,
            String userIntent
    ) {
        this.stepName = stepName;
        this.assistantMessage = assistantMessage;
        this.userIntent = userIntent;
    }
}