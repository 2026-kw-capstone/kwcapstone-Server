package com.kwcapstone.server.domain.mysentence.service;

import com.kwcapstone.server.domain.mysentence.dto.request.MySentenceCreateReqDTO;
import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceCreateResDTO;
import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceDeleteResDTO;

public interface MySentenceCommandService {
    MySentenceCreateResDTO createMySentence(MySentenceCreateReqDTO request);  // 나만의 문장 생성
    MySentenceDeleteResDTO deleteMySentence(Long sentenceId);  // 나만의 문장 삭제
}
