package com.kwcapstone.server.domain.conversation.repository;

import com.kwcapstone.server.domain.conversation.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    Optional<Conversation> findByIdAndMemberId(Long conversationId, Long memberId);
    List<Conversation> findAllByMemberIdOrderByLastMessageAtDesc(Long memberId);
}
