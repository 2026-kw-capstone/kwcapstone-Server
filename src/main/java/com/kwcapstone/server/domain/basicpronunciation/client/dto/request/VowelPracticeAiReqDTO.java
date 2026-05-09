package com.kwcapstone.server.domain.basicpronunciation.client.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VowelPracticeAiReqDTO {
    private String s3Url;
    private String targetVowel;
}
