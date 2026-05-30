package com.kwcapstone.server.domain.scenario.repository;

import com.kwcapstone.server.domain.scenario.entity.ScenarioLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScenarioLevelRepository extends JpaRepository<ScenarioLevel, Long> {
    List<ScenarioLevel> findAllByScenarioIdAndDeletedAtIsNullOrderByLevelNoAsc(Long scenarioId);
    Optional<ScenarioLevel> findByScenarioIdAndLevelNoAndDeletedAtIsNull(Long scenarioId, Integer levelNo);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update ScenarioLevel level
            set level.deletedAt = :deletedAt
            where level.scenario.id = :scenarioId
            and level.deletedAt is null
            """)
    void softDeleteAllByScenarioId(
            @Param("scenarioId") Long scenarioId,
            @Param("deletedAt") LocalDateTime deletedAt
    );
}
