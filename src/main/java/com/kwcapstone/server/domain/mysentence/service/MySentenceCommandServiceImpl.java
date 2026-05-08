package com.kwcapstone.server.domain.mysentence.service;

import com.kwcapstone.server.domain.member.entity.Member;
import com.kwcapstone.server.domain.member.repository.MemberRepository;
import com.kwcapstone.server.domain.mysentence.converter.MySentenceConverter;
import com.kwcapstone.server.domain.mysentence.dto.request.MySentenceCreateReqDTO;
import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceCreateResDTO;
import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceDeleteResDTO;
import com.kwcapstone.server.domain.mysentence.entity.MySentence;
import com.kwcapstone.server.domain.mysentence.exception.code.MySentenceErrorCode;
import com.kwcapstone.server.domain.mysentence.repository.MySentenceRepository;
import com.kwcapstone.server.global.apiPayload.exception.CustomException;
import com.kwcapstone.server.global.apiPayload.response.ErrorCode;
import com.kwcapstone.server.global.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MySentenceCommandServiceImpl implements MySentenceCommandService {

    private static final int MAX_SENTENCE_LENGTH = 300;

    private final MySentenceRepository mySentenceRepository;
    private final MemberRepository memberRepository;

    @Override
    public MySentenceCreateResDTO createMySentence(MySentenceCreateReqDTO request) {
        validateSentenceContent(request.getSentenceContent());

        Long memberId = SecurityUtil.getCurrentMemberId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        MySentence mySentence = MySentenceConverter.toMySentence(request, member);
        MySentence savedMySentence = mySentenceRepository.save(mySentence);

        return MySentenceConverter.toCreateResponse(savedMySentence);
    }

    @Override
    public MySentenceDeleteResDTO deleteMySentence(Long sentenceId) {
        Long memberId = SecurityUtil.getCurrentMemberId();

        MySentence mySentence = mySentenceRepository.findByIdAndDeletedAtIsNull(sentenceId)
                .orElseThrow(() -> new CustomException(MySentenceErrorCode.MY_SENTENCE_NOT_FOUND));

        if(!mySentence.getMember().getId().equals(memberId)) {
            throw new CustomException(MySentenceErrorCode.MY_SENTENCE_FORBIDDEN);
        }

        mySentence.softDelete();

        return new MySentenceDeleteResDTO(mySentence.getId());
    }

    private void validateSentenceContent(String sentenceContent) {
        if (sentenceContent == null || sentenceContent.trim().isEmpty()) {
            throw new CustomException(MySentenceErrorCode.EMPTY_SENTENCE_CONTENT);
        }

        if (sentenceContent.length() > MAX_SENTENCE_LENGTH) {
            throw new CustomException(MySentenceErrorCode.SENTENCE_LENGTH_EXCEEDED);
        }
    }
}
