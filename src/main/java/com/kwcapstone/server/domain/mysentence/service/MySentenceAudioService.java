package com.kwcapstone.server.domain.mysentence.service;

import com.kwcapstone.server.domain.mysentence.dto.request.MySentenceAnalyzeReqDTO;
import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceAnalyzeResDTO;
import com.kwcapstone.server.domain.mysentence.client.dto.response.MySentenceTtsAiResDTO;
import com.kwcapstone.server.domain.mysentence.dto.response.MySentenceUserAudioResDTO;

public interface MySentenceAudioService {
    MySentenceTtsAiResDTO getTts(Long sentenceId);  // TTS
    MySentenceUserAudioResDTO getUserAudio(Long sentenceId);  // user audio
    MySentenceAnalyzeResDTO analyzePronunciation(MySentenceAnalyzeReqDTO request);  // 발음 분석
}
