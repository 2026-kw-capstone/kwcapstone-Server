package com.kwcapstone.server.domain.scenario.repository;

import com.kwcapstone.server.domain.scenario.entity.ScenarioLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScenarioLevelRepository extends JpaRepository<ScenarioLevel, Long> {
    List<ScenarioLevel> findAllByScenarioIdOrderByLevelNoAsc(Long scenarioId);
}
