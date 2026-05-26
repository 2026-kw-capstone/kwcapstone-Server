package com.kwcapstone.server.domain.basicpronunciation.repository;

import com.kwcapstone.server.domain.basicpronunciation.entity.BasicPronunciationPractice;
import com.kwcapstone.server.domain.basicpronunciation.enums.BasicVowel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface BasicPronunciationPracticeRepository extends JpaRepository<BasicPronunciationPractice, Long> {
    Optional<BasicPronunciationPractice> findByMemberIdAndClientRequestId(Long memberId, String clientRequestId);
    Optional<BasicPronunciationPractice> findTopByMemberIdAndTargetVowelOrderByCreatedAtDesc(Long memberId, BasicVowel targetVowel);

    @Query("""
        select avg(p.accuracyScore)
        from BasicPronunciationPractice p
        where p.member.id = :memberId
            and p.createdAt >= :start
            and p.createdAt < :end
    """)
    Double findAverageAccuracyScore(
            @Param("memberId") Long memberId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    long countByMemberIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(Long memberId, LocalDateTime start, LocalDateTime end);

    @Query("""
        select coalesce(sum(p.accuracyScore), 0)
        from BasicPronunciationPractice p
        where p.member.id = :memberId
            and p.createdAt >= :start
            and p.createdAt < :end
    """)
    BigDecimal sumAccuracyScore(
            @Param("memberId") Long memberId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
