package com.kwcapstone.server.domain.mysentence.service;

import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceAnalyzeResDTO;
import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceTtsResDTO;
import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceUserAudioResDTO;
import org.springframework.web.multipart.MultipartFile;

public interface MySentenceAudioService {
    MySentenceTtsResDTO getTts(Long sentenceId);  // TTS
    MySentenceUserAudioResDTO getUserAudio(Long sentenceId);  // user audio
    MySentenceAnalyzeResDTO analyzePronunciation(Long sentenceId, MultipartFile voiceFile);  // 발음 분석
}
