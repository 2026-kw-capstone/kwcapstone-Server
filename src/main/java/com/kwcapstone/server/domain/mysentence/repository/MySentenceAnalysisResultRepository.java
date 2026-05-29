package com.kwcapstone.server.domain.mysentence.repository;

import com.kwcapstone.server.domain.mysentence.entity.MySentenceAnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface MySentenceAnalysisResultRepository extends JpaRepository<MySentenceAnalysisResult, Long> {
    Optional<MySentenceAnalysisResult> findTopByMySentenceIdAndMemberIdOrderByCreatedAtDesc(Long mySentenceId, Long memberId);
    Optional<MySentenceAnalysisResult> findByMemberIdAndClientRequestId(Long memberId, String clientRequestId);
    long countByMemberIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(Long memberId, LocalDateTime startAt, LocalDateTime endAt);

    @Query("""
            select sum(result.pronunciationScore)
            from MySentenceAnalysisResult result
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
        select avg(r.pronunciationScore)
        from MySentenceAnalysisResult r
        where r.member.id = :memberId
            and r.createdAt >= :start
            and r.createdAt < :end
    """)
    Double findAveragePronunciationScore(
            @Param("memberId") Long memberId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
        select coalesce(sum(r.pronunciationScore), 0)
        from MySentenceAnalysisResult r
        where r.member.id = :memberId
            and r.createdAt >= :start
            and r.createdAt < :end
    """)
    BigDecimal sumPronunciationScore(
            @Param("memberId") Long memberId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}