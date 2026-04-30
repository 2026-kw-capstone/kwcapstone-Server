package com.kwcapstone.server.domain.conversation.client;

import com.kwcapstone.server.domain.conversation.client.dto.request.FreeTalkAiReqDTO;
import com.kwcapstone.server.domain.conversation.client.dto.request.SttAiReqDTO;
import com.kwcapstone.server.domain.conversation.client.dto.response.FreeTalkAiResDTO;
import com.kwcapstone.server.domain.conversation.client.dto.response.SttAiResDTO;

public interface FreeTalkAiClient {
    FreeTalkAiResDTO sendText(FreeTalkAiReqDTO request);
    SttAiResDTO transcribe(SttAiReqDTO request);
}
