package com.kwcapstone.server.domain.conversation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class VoiceMessageSendReqDTO {
    private Long conversationId;

    @NotBlank
    private String clientRequestId;

    @NotNull
    private MultipartFile voiceFile;
}
