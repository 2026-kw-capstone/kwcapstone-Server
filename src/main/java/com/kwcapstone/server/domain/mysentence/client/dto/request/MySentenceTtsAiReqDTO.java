package com.kwcapstone.server.domain.mysentence.client.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MySentenceTtsAiReqDTO {
    private String text;
    private String voice;
    private Double speed;
}
