package com.kwcapstone.server.domain.scenario.entity;

import com.kwcapstone.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "scenario_level")
@AttributeOverride(name = "id", column = @Column(name = "scenario_level_id"))
public class ScenarioLevel extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_id", nullable = false)
    private Scenario scenario;

    @Column(name = "level_no", nullable = false)
    private Integer levelNo;

    @Column(name = "level_title", nullable = false, length = 60)
    private String levelTitle;

    @Column(name = "level_description", nullable = false, length = 255)
    private String levelDescription;

    @Builder.Default
    @OneToMany(mappedBy = "scenarioLevel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScenarioStep> steps = new ArrayList<>();

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public void addStep(ScenarioStep step) {
        this.steps.add(step);
        step.setScenarioLevel(this);
    }

    public void updateContent(String levelTitle, String levelDescription) {
        this.levelTitle = levelTitle;
        this.levelDescription = levelDescription;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}