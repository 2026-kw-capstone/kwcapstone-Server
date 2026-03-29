package com.kwcapstone.server.domain.conversation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConversationTitleUpdateReqDTO {
    @NotBlank
    @Size(max = 200)
    private String title;
}
