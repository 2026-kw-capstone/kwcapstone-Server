package com.kwcapstone.server.domain.mysentence.service;

import com.kwcapstone.server.domain.mysentence.converter.MySentenceConverter;
import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceDetailResDTO;
import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceListResDTO;
import com.kwcapstone.server.domain.mysentence.entity.MySentence;
import com.kwcapstone.server.domain.mysentence.exception.code.MySentenceErrorCode;
import com.kwcapstone.server.domain.mysentence.repository.MySentenceRepository;
import com.kwcapstone.server.global.apiPayload.exception.CustomException;
import com.kwcapstone.server.global.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MySentenceQueryServiceImpl implements MySentenceQueryService {

    private final MySentenceRepository mySentenceRepository;

    @Override
    public MySentenceListResDTO getMySentences() {
        Long memberId = SecurityUtil.getCurrentMemberId();

        List<MySentence> mySentences = mySentenceRepository.findAllByMemberIdAndDeletedAtIsNullOrderByCreatedAtDesc(memberId);

        return MySentenceConverter.toListResponse(mySentences);
    }

    @Override
    public MySentenceDetailResDTO getMySentenceDetail(Long sentenceId) {
        Long memberId = SecurityUtil.getCurrentMemberId();

        MySentence mySentence = mySentenceRepository.findByIdAndDeletedAtIsNull(sentenceId)
                .orElseThrow(() -> new CustomException(MySentenceErrorCode.MY_SENTENCE_NOT_FOUND));

        if (!mySentence.getMember().getId().equals(memberId)) {
            throw new CustomException(MySentenceErrorCode.MY_SENTENCE_FORBIDDEN);
        }

        return MySentenceConverter.toDetailResponse(mySentence);
    }
}
