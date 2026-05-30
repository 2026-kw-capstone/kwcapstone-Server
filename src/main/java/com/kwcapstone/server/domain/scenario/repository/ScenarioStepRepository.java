package com.kwcapstone.server.domain.scenario.repository;

import com.kwcapstone.server.domain.scenario.entity.ScenarioStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScenarioStepRepository extends JpaRepository<ScenarioStep, Long> {
    Optional<ScenarioStep> findByScenarioLevelIdAndStepNoAndDeletedAtIsNull(Long scenarioLevelId, Integer stepNo);
    long countByScenarioLevelIdAndDeletedAtIsNull(Long scenarioLevelId);
    List<ScenarioStep> findAllByScenarioLevelIdAndDeletedAtIsNullOrderByStepNoAsc(Long scenarioLevelId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update ScenarioStep step
            set step.deletedAt = :deletedAt
            where step.scenarioLevel.id in (
                select level.id
                from ScenarioLevel level
                where level.scenario.id = :scenarioId
            )
            and step.deletedAt is null
            """)
    void softDeleteAllByScenarioId(
            @Param("scenarioId") Long scenarioId,
            @Param("deletedAt") LocalDateTime deletedAt
    );
}