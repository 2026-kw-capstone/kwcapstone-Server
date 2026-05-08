package com.kwcapstone.server.domain.mysentence.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MySentenceListResDTO {
    private List<MySentenceInfo> sentences;

    @Getter
    @AllArgsConstructor
    public static class MySentenceInfo{
        private Long sentenceId;
        private String sentenceContent;
    }
}
