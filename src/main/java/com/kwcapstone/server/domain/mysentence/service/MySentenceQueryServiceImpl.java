package com.kwcapstone.server.domain.mysentence.service;

import com.kwcapstone.server.domain.mysentence.converter.MySentenceConverter;
import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceListResDTO;
import com.kwcapstone.server.domain.mysentence.entity.MySentence;
import com.kwcapstone.server.domain.mysentence.repository.MySentenceRepository;
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
}
