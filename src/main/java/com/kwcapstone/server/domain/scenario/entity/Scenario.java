package com.kwcapstone.server.domain.scenario.entity;

import com.kwcapstone.server.domain.member.entity.Member;
import com.kwcapstone.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "scenario")
@AttributeOverride(name = "id", column = @Column(name = "scenario_id"))
public class Scenario extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Column(name = "scenario_context", nullable = false, length = 100)
    private String scenarioContext;

    @Column(name = "goal", nullable = false, length = 255)
    private String goal;

    @Builder.Default
    @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScenarioLevel> levels = new ArrayList<>();

    public void addLevel(ScenarioLevel level) {
        this.levels.add(level);
        level.setScenario(this);
    }
}