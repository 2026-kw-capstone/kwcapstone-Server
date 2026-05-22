package com.kwcapstone.server.domain.mysentence.repository;

import com.kwcapstone.server.domain.mysentence.entity.MySentenceAnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MySentenceAnalysisResultRepository extends JpaRepository<MySentenceAnalysisResult, Long> {
    Optional<MySentenceAnalysisResult> findTopByMySentenceIdAndMemberIdOrderByCreatedAtDesc(Long mySentenceId, Long memberId);
    Optional<MySentenceAnalysisResult> findByMemberIdAndClientRequestId(Long memberId, String clientRequestId);
}
