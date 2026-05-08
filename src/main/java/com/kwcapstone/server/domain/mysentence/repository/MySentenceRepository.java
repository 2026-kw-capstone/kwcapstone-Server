package com.kwcapstone.server.domain.mysentence.repository;

import com.kwcapstone.server.domain.mysentence.entity.MySentence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MySentenceRepository extends JpaRepository<MySentence, Long> {
    List<MySentence> findAllByMemberIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long memberId);
    Optional<MySentence> findByIdAndMemberIdAndDeletedAtIsNull(Long sentenceId, Long memberId);
    boolean existsByIdAndMemberIdAndDeletedAtIsNull(Long sentenceId, Long memberId);
}
