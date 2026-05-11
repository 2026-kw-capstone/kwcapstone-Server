package com.kwcapstone.server.domain.member.service;

import com.kwcapstone.server.domain.member.converter.MyPageConverter;
import com.kwcapstone.server.domain.member.dto.response.MyPageMeResDTO;
import com.kwcapstone.server.domain.member.entity.Member;
import com.kwcapstone.server.domain.member.exception.code.MemberErrorCode;
import com.kwcapstone.server.domain.member.repository.MemberRepository;
import com.kwcapstone.server.global.apiPayload.exception.CustomException;
import com.kwcapstone.server.global.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageServiceImpl implements MyPageService {
    private final MemberRepository memberRepository;

    @Override
    public MyPageMeResDTO getMyInfo() {
        Long memberId = SecurityUtil.getCurrentMemberId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        return MyPageConverter.toMyPageMeResDTO(member);
    }
}
