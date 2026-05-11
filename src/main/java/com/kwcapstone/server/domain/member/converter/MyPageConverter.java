package com.kwcapstone.server.domain.member.converter;

import com.kwcapstone.server.domain.member.dto.response.MyPageMeResDTO;
import com.kwcapstone.server.domain.member.dto.response.UpdateNicknameResDTO;
import com.kwcapstone.server.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MyPageConverter {
    public static MyPageMeResDTO toMyPageMeResDTO(Member member) {
        return new MyPageMeResDTO(
                member.getId(),
                member.getEmail(),
                member.getNickname()
        );
    }

    public static UpdateNicknameResDTO toUpdateNicknameResDTO(String nickname) {
        return new UpdateNicknameResDTO(nickname);
    }
}
