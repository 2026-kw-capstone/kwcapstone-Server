package com.kwcapstone.server.domain.conversation.client.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SttAiReqDTO {
    private String s3Url;
}
