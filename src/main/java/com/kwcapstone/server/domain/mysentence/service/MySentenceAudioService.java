package com.kwcapstone.server.domain.mysentence.service;

import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceTtsResDTO;
import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceUserAudioResDTO;

public interface MySentenceAudioService {
    MySentenceTtsResDTO getTts(Long sentenceId);  // TTS
    MySentenceUserAudioResDTO getUserAudio(Long sentenceId);  // user audio
}
