package com.kwcapstone.server.domain.scenario.service;

import com.kwcapstone.server.domain.scenario.dto.response.ScenarioAnswerAnalyzeResDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioUserAudioResDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ScenarioAudioService {
    ScenarioAnswerAnalyzeResDTO analyzeScenarioAnswer(Long scenarioId, Integer level, Integer stepNo, MultipartFile voiceFile);  // 음성 파일 분석
    ScenarioUserAudioResDTO getScenarioUserAudio(Long scenarioId, Integer level, Integer stepNo);  // 내 음성 듣기
}
