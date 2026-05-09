package com.kwcapstone.server.domain.basicpronunciation.repository;

import com.kwcapstone.server.domain.basicpronunciation.entity.BasicPronunciationPractice;
import com.kwcapstone.server.domain.basicpronunciation.enums.BasicVowel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BasicPronunciationPracticeRepository extends JpaRepository<BasicPronunciationPractice, Long> {
    Optional<BasicPronunciationPractice> findByMemberIdAndClientRequestId(Long memberId, String clientRequestId);
    Optional<BasicPronunciationPractice> findTopByMemberIdAndTargetVowelOrderByCreatedAtDesc(Long memberId, BasicVowel targetVowel);
}
