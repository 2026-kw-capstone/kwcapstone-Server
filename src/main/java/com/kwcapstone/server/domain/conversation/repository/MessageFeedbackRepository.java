package com.kwcapstone.server.domain.conversation.repository;

import com.kwcapstone.server.domain.conversation.entity.MessageFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageFeedbackRepository extends JpaRepository<MessageFeedback, Long> {
    Optional<MessageFeedback> findByMessageId(Long messageId);
}
