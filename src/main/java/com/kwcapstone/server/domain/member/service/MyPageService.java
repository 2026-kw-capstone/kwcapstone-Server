package com.kwcapstone.server.domain.member.service;

import com.kwcapstone.server.domain.member.dto.response.MyPageMeResDTO;
import com.kwcapstone.server.domain.member.dto.response.UpdateNicknameResDTO;

public interface MyPageService {
    MyPageMeResDTO getMyInfo();
    UpdateNicknameResDTO updateMyNickname(String nickname);
}
