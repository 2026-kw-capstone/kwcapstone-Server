package com.kwcapstone.server.domain.auth.converter;

import com.kwcapstone.server.domain.auth.dto.response.AuthLoginResDTO;
import com.kwcapstone.server.domain.auth.dto.response.AuthSignUpResDTO;
import com.kwcapstone.server.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthConverter {
    // 회원가입 응답
    public static AuthSignUpResDTO toSignUpResDTO(Member member) {
        return new AuthSignUpResDTO(
                member.getId(),
                member.getEmail(),
                member.getNickname()
        );
    }

    // 로그인 응답
    public static AuthLoginResDTO toLoginResDTO(String accessToken, Member member) {
        return new AuthLoginResDTO(
                accessToken,
                new AuthLoginResDTO.MemberInfo(
                        member.getId(),
                        member.getEmail(),
                        member.getNickname()
                )
        );
    }
}
