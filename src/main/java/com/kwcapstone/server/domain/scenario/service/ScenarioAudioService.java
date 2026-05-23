package com.kwcapstone.server.domain.scenario.service;

import com.kwcapstone.server.domain.scenario.dto.request.ScenarioAnswerAnalyzeReqDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioAnswerAnalyzeResDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioUserAudioResDTO;


public interface ScenarioAudioService {
    ScenarioAnswerAnalyzeResDTO analyzeScenarioAnswer(ScenarioAnswerAnalyzeReqDTO request);  // 음성 파일 분석
    ScenarioUserAudioResDTO getScenarioUserAudio(Long scenarioId, Integer level, Integer stepNo);  // 내 음성 듣기
}
