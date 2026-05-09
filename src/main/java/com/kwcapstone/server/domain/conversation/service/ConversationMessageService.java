package com.kwcapstone.server.domain.conversation.service;

import com.kwcapstone.server.domain.conversation.dto.request.TextMessageSendReqDTO;
import com.kwcapstone.server.domain.conversation.dto.request.VoiceMessageSendReqDTO;
import com.kwcapstone.server.domain.conversation.dto.response.MessageSendResDTO;
import com.kwcapstone.server.domain.conversation.dto.response.MessageVoiceUrlResDTO;

public interface ConversationMessageService {
    MessageSendResDTO sendTextMessage(TextMessageSendReqDTO request);
    MessageSendResDTO sendVoiceMessage(VoiceMessageSendReqDTO request);
    MessageVoiceUrlResDTO getMessageVoiceUrl(Long messageId);
}
