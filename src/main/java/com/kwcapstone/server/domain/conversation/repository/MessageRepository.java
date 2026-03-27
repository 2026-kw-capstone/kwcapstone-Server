package com.kwcapstone.server.domain.conversation.repository;

import com.kwcapstone.server.domain.conversation.entity.Message;
import com.kwcapstone.server.domain.conversation.enums.MessageRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // 사용자 기준 메시지 조회
    @Query("""
        select m
        from Message m
        join m.conversation c
        where c.member.id = :memberId
            and m.clientRequestId = :clientRequestId
            and m.role = :role
    """)
    Optional<Message> findByMemberIdAndClientRequestIdAndRole(
            @Param("memberId") Long memberId,
            @Param("clientRequestId") String clientRequestId,
            @Param("role") MessageRole role
    );

    // 채팅방 기준 메시지 조회
    Optional<Message> findByConversationIdAndClientRequestIdAndRole(
            Long conversationId,
            String clientRequestId,
            MessageRole role
    );
}
