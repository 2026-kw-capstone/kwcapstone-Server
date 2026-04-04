package com.kwcapstone.server.domain.conversation.client;

import com.kwcapstone.server.domain.conversation.client.dto.request.FreeTalkAiReqDTO;
import com.kwcapstone.server.domain.conversation.client.dto.response.FreeTalkAiResDTO;
import com.kwcapstone.server.global.apiPayload.exception.CustomException;
import com.kwcapstone.server.global.apiPayload.response.ErrorCode;
import com.kwcapstone.server.global.config.properties.AiServerProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
@RequiredArgsConstructor
public class HttpFreeTalkAiClient implements FreeTalkAiClient {
    private final RestClient aiRestClient;
    private final AiServerProperties properties;

    @Override
    public FreeTalkAiResDTO sendText(FreeTalkAiReqDTO request) {
        try {
            FreeTalkAiResDTO response = aiRestClient.post()
                    .uri("/chat/free-talk")
                    .body(request)
                    .retrieve()
                    .body(FreeTalkAiResDTO.class);

            if (response == null) {
                log.error("AI server returned null response. uri=/chat/free-talk");

                throw new CustomException(ErrorCode.AI_SERVER_ERROR);
            }

            if (Boolean.FALSE.equals(response.getSuccess())) {
                log.error("AI server returned failure response. uri=/chat/free-talk, success={}",
                        response.getSuccess());

                throw new CustomException(ErrorCode.AI_SERVER_ERROR);
            }

            return response;
        } catch (RestClientException e) {
            log.error("Failed to call AI server. uri=/chat/free-talk, message={}", e.getMessage(), e);

            throw new CustomException(ErrorCode.AI_SERVER_ERROR);
        }
    }
}
