package com.kwcapstone.server.domain.mysentence.service;

import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceTtsResDTO;

public interface MySentenceAudioService {
    MySentenceTtsResDTO getTts(Long sentenceId);  // TTS
}
