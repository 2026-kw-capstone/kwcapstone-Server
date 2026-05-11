package com.kwcapstone.server.domain.scenario.repository;

import com.kwcapstone.server.domain.scenario.entity.ScenarioAnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScenarioAnalysisResultRepository extends JpaRepository<ScenarioAnalysisResult, Long> {
    boolean existsByScenarioStepIdAndMemberId(Long scenarioStepId, Long memberId);
}