package com.kwcapstone.server.domain.mysentence.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PronunciationAnalyzeAiReqDTO {
    private String s3Url;
    private String referenceText;
}
