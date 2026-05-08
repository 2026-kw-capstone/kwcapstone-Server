package com.kwcapstone.server.domain.mysentence.service;

import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceDetailResDTO;
import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceListResDTO;

public interface MySentenceQueryService {
    // 저장된 문장 목록 조회
    MySentenceListResDTO getMySentences();
    // 선택된 문장 상세 조회
    MySentenceDetailResDTO getMySentenceDetail(Long sentenceId);
}
