package com.kwcapstone.server.domain.mysentence.repository;

import com.kwcapstone.server.domain.mysentence.entity.MySentence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MySentenceRepository extends JpaRepository<MySentence, Long> {
    // 저장된 문장 목록 조회
    List<MySentence> findAllByMemberIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long memberId);
    // 나만의 문장 삭제
    Optional<MySentence> findByIdAndDeletedAtIsNull(Long sentenceId);
}
