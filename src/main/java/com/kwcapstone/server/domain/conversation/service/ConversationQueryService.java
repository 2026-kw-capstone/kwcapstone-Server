package com.kwcapstone.server.domain.conversation.service;

import com.kwcapstone.server.domain.conversation.dto.response.ConversationDetailResDTO;
import com.kwcapstone.server.domain.conversation.dto.response.ConversationListItemResDTO;

import java.util.List;

public interface ConversationQueryService {
    List<ConversationListItemResDTO> getConversations();
    ConversationDetailResDTO getConversationDetail(Long conversationId);
}
