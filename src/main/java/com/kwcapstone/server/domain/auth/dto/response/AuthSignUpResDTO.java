package com.kwcapstone.server.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthSignUpResDTO { // 회원가입 응답 DTO
    private Long memberId;
    private String email;
    private String nickname;
}
