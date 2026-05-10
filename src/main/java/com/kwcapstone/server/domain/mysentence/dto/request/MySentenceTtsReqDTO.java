package com.kwcapstone.server.domain.mysentence.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MySentenceTtsReqDTO {
    private String text;
    private String voice;
    private Double speed;
}
