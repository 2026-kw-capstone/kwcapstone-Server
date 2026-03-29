package com.kwcapstone.server.domain.conversation.service;

import com.kwcapstone.server.domain.conversation.dto.request.ConversationTitleUpdateReqDTO;
import com.kwcapstone.server.domain.conversation.dto.response.ConversationTitleUpdateResDTO;

public interface ConversationCommandService {
    ConversationTitleUpdateResDTO updateConversationTitle(Long conversationId, ConversationTitleUpdateReqDTO request);
    void deleteConversation(Long conversationId);
}
