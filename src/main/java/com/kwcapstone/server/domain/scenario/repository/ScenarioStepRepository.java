package com.kwcapstone.server.domain.scenario.repository;

import com.kwcapstone.server.domain.scenario.entity.ScenarioStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScenarioStepRepository extends JpaRepository<ScenarioStep, Long> {
    Optional<ScenarioStep> findByScenarioLevelIdAndStepNo(Long scenarioLevelId, Integer stepNo);
    long countByScenarioLevelId(Long scenarioLevelId);
}