package com.kwcapstone.server.global.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthPrincipal { // JWT 인증 후 SecurityContext에 들어가는 로그인 사용자 정보
    private final Long memberId;
    private final String email;
}
