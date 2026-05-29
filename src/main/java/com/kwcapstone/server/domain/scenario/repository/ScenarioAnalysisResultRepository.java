package com.kwcapstone.server.domain.scenario.repository;

import com.kwcapstone.server.domain.scenario.entity.ScenarioAnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface ScenarioAnalysisResultRepository extends JpaRepository<ScenarioAnalysisResult, Long> {
    boolean existsByScenarioStepIdAndMemberId(Long scenarioStepId, Long memberId);
    Optional<ScenarioAnalysisResult> findTopByScenarioStepIdAndMemberIdOrderByCreatedAtDesc(Long scenarioStepId, Long memberId);
    Optional<ScenarioAnalysisResult> findTopByMemberIdOrderByCreatedAtDesc(Long memberId);
    Optional<ScenarioAnalysisResult> findByMemberIdAndClientRequestId(Long memberId, String clientRequestId);
    long countByMemberIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(Long memberId, LocalDateTime startAt, LocalDateTime endAt);

    @Query("""
            select sum(result.pronunciationScore)
            from ScenarioAnalysisResult result
            where result.member.id = :memberId
            and result.createdAt >= :startAt
            and result.createdAt < :endAt
            """)
    BigDecimal sumPronunciationScoreByMemberIdAndPeriod(
            @Param("memberId") Long memberId,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt
    );

    @Query("""
            select sum(result.meaningDeliveryScore)
            from ScenarioAnalysisResult result
            where result.member.id = :memberId
            and result.createdAt >= :startAt
            and result.createdAt < :endAt
            """)
    BigDecimal sumMeaningDeliveryScoreByMemberIdAndPeriod(
            @Param("memberId") Long memberId,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt
    );

    @Query("""
        select avg(result.pronunciationScore)
        from ScenarioAnalysisResult result
        where result.member.id = :memberId
            and result.createdAt >= :start
            and result.createdAt < :end
    """)
    Double findAveragePronunciationScore(
            @Param("memberId") Long memberId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
        select coalesce(sum(result.pronunciationScore), 0)
        from ScenarioAnalysisResult result
        where result.member.id = :memberId
            and result.createdAt >= :start
            and result.createdAt < :end
    """)
    BigDecimal sumPronunciationScore(
            @Param("memberId") Long memberId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
        select avg(result.meaningDeliveryScore)
        from ScenarioAnalysisResult result
        where result.member.id = :memberId
            and result.createdAt >= :start
            and result.createdAt < :end
    """)
    Double findAverageMeaningDeliveryScore(
            @Param("memberId") Long memberId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

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