package com.kwcapstone.server.domain.conversation.client;

import com.kwcapstone.server.domain.conversation.client.dto.request.FreeTalkAiReqDTO;
import com.kwcapstone.server.domain.conversation.client.dto.response.FreeTalkAiResDTO;

public interface FreeTalkAiClient {
    FreeTalkAiResDTO sendText(FreeTalkAiReqDTO request);
}
