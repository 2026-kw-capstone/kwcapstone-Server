package com.kwcapstone.server.domain.scenario.repository;

import com.kwcapstone.server.domain.scenario.entity.ScenarioAnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScenarioAnalysisResultRepository extends JpaRepository<ScenarioAnalysisResult, Long> {
    boolean existsByScenarioStepIdAndMemberId(Long scenarioStepId, Long memberId);
    Optional<ScenarioAnalysisResult> findTopByScenarioStepIdAndMemberIdOrderByCreatedAtDesc(Long scenarioStepId, Long memberId);
}