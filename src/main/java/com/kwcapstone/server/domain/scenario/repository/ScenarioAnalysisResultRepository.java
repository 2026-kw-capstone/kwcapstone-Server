package com.kwcapstone.server.domain.scenario.repository;

import com.kwcapstone.server.domain.scenario.entity.ScenarioAnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface ScenarioAnalysisResultRepository extends JpaRepository<ScenarioAnalysisResult, Long> {
    boolean existsByScenarioStepIdAndMemberId(Long scenarioStepId, Long memberId);
    Optional<ScenarioAnalysisResult> findTopByScenarioStepIdAndMemberIdOrderByCreatedAtDesc(Long scenarioStepId, Long memberId);
    Optional<ScenarioAnalysisResult> findByMemberIdAndClientRequestId(Long memberId, String clientRequestId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            delete from ScenarioAnalysisResult result
            where result.member.id = :memberId
            and result.scenarioStep.id in :scenarioStepIds
            """)
    void deleteAllByMemberIdAndScenarioStepIdIn(
            @Param("memberId") Long memberId,
            @Param("scenarioStepIds") Collection<Long> scenarioStepIds
    );
}