package com.kwcapstone.server.domain.mysentence.service;

import com.kwcapstone.server.domain.mysentence.dto.request.MySentenceCreateReqDTO;
import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceCreateResDTO;

public interface MySentenceCommandService {
    MySentenceCreateResDTO createMySentence(MySentenceCreateReqDTO request);
}
