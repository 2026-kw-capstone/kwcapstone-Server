package com.kwcapstone.server.domain.basicpronunciation.client.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TtsAiReqDTO {
    private String text;
    private String voice;
    private Double speed;
}
