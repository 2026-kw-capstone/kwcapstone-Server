package com.kwcapstone.server.domain.conversation.repository;

import com.kwcapstone.server.domain.conversation.entity.MessageFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageFeedbackRepository extends JpaRepository<MessageFeedback, Long> {
    Optional<MessageFeedback> findByMessageId(Long messageId);
    List<MessageFeedback> findAllByMessageConversationId(Long conversationId);

    @Modifying
    @Query("""
        delete from MessageFeedback mf
        where mf.message.conversation.id = :conversationId
    """)
    void deleteAllByConversationId(@Param("conversationId") Long conversationId);
}
