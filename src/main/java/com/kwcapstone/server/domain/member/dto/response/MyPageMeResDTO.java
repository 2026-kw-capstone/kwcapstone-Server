package com.kwcapstone.server.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyPageMeResDTO {
    private Long memberId;
    private String email;
    private String nickname;
}
