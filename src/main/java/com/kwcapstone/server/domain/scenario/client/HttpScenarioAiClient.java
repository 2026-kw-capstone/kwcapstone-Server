package com.kwcapstone.server.domain.scenario.client;

import com.kwcapstone.server.domain.scenario.dto.request.ScenarioGenerateAiReqDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioGenerateAiResDTO;
import com.kwcapstone.server.domain.scenario.exception.code.ScenarioErrorCode;
import com.kwcapstone.server.global.apiPayload.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class HttpScenarioAiClient implements ScenarioAiClient {

    private final RestClient aiRestClient;

    @Override
    public ScenarioGenerateAiResDTO generateScenario(ScenarioGenerateAiReqDTO request) {
        try {
            return aiRestClient.post()
                    .uri("/generate-scenario")
                    .body(request)
                    .retrieve()
                    .body(ScenarioGenerateAiResDTO.class);
        } catch (Exception e) {
            throw new CustomException(ScenarioErrorCode.SCENARIO_GENERATION_FAILED);
        }
    }
}