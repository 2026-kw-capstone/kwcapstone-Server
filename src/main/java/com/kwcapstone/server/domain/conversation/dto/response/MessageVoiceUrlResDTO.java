package com.kwcapstone.server.domain.conversation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessageVoiceUrlResDTO {
    private Long messageId;
    private String voiceUrl;
}
