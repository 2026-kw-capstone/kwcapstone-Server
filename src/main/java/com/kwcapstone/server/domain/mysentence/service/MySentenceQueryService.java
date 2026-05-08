package com.kwcapstone.server.domain.mysentence.service;

import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceListResDTO;

public interface MySentenceQueryService {
    // 저장된 문장 목록 조회
    MySentenceListResDTO getMySentences();
}
