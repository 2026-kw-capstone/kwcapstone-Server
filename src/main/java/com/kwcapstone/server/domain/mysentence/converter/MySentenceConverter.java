package com.kwcapstone.server.domain.mysentence.converter;

import com.kwcapstone.server.domain.member.entity.Member;
import com.kwcapstone.server.domain.mysentence.dto.request.MySentenceCreateReqDTO;
import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceCreateResDTO;
import com.kwcapstone.server.domain.mysentence.entity.MySentence;

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
}
