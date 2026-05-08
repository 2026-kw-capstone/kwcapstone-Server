package com.kwcapstone.server.domain.mysentence.converter;

import com.kwcapstone.server.domain.member.entity.Member;
import com.kwcapstone.server.domain.mysentence.dto.request.MySentenceCreateReqDTO;
import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceCreateResDTO;
import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceDetailResDTO;
import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceListResDTO;
import com.kwcapstone.server.domain.mysentence.entity.MySentence;

import java.util.List;

public class MySentenceConverter {

    private MySentenceConverter() {
    }

    public static MySentence toMySentence(MySentenceCreateReqDTO request, Member member) {
        return MySentence.builder()
                .member(member)
                .sentenceContent(request.getSentenceContent().trim())
                .build();
    }

    public static MySentenceCreateResDTO toCreateResponse(MySentence mySentence) {
        return new MySentenceCreateResDTO(
                mySentence.getId(),
                mySentence.getSentenceContent()
        );
    }

    public static MySentenceListResDTO.MySentenceInfo toMySentenceInfo(MySentence mySentence) {
        return new MySentenceListResDTO.MySentenceInfo(
                mySentence.getId(),
                mySentence.getSentenceContent()
        );
    }

    public static MySentenceListResDTO toListResponse(List<MySentence> mySentences) {
        return new MySentenceListResDTO(
                mySentences.stream()
                        .map(MySentenceConverter::toMySentenceInfo)
                        .toList()
        );
    }

    public static MySentenceDetailResDTO toDetailResponse(MySentence mySentence) {
        return new MySentenceDetailResDTO(
                mySentence.getId(),
                mySentence.getSentenceContent()
        );
    }
}
