package com.kwcapstone.server.domain.scenario.service;

import com.kwcapstone.server.domain.member.entity.Member;
import com.kwcapstone.server.domain.member.repository.MemberRepository;
import com.kwcapstone.server.domain.scenario.client.ScenarioAiClient;
import com.kwcapstone.server.domain.scenario.converter.ScenarioConverter;
import com.kwcapstone.server.domain.scenario.dto.request.ScenarioCreateReqDTO;
import com.kwcapstone.server.domain.scenario.dto.request.ScenarioGenerateAiReqDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioCreateResDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioGenerateAiResDTO;
import com.kwcapstone.server.domain.scenario.entity.Scenario;
import com.kwcapstone.server.domain.scenario.exception.code.ScenarioErrorCode;
import com.kwcapstone.server.domain.scenario.repository.ScenarioRepository;
import com.kwcapstone.server.global.apiPayload.exception.CustomException;
import com.kwcapstone.server.global.apiPayload.response.ErrorCode;
import com.kwcapstone.server.global.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ScenarioCommandServiceImpl implements ScenarioCommandService {

    private final ScenarioRepository scenarioRepository;
    private final MemberRepository memberRepository;
    private final ScenarioAiClient scenarioAiClient;

    @Override
    public ScenarioCreateResDTO createScenario(ScenarioCreateReqDTO request) {
        validateCreateRequest(request);

        Long memberId = SecurityUtil.getCurrentMemberId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        ScenarioGenerateAiResDTO aiResponse = scenarioAiClient.generateScenario(
                new ScenarioGenerateAiReqDTO(
                        request.getTitle().trim(),
                        request.getDescription().trim()
                )
        );

        validateAiResponse(aiResponse);

        Scenario scenario = ScenarioConverter.toScenario(request, member, aiResponse);
        Scenario savedScenario = scenarioRepository.save(scenario);

        return ScenarioConverter.toCreateResponse(savedScenario);
    }

    private void validateCreateRequest(ScenarioCreateReqDTO request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new CustomException(ScenarioErrorCode.EMPTY_SCENARIO_TITLE);
        }

        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new CustomException(ScenarioErrorCode.EMPTY_SCENARIO_DESCRIPTION);
        }
    }

    private void validateAiResponse(ScenarioGenerateAiResDTO aiResponse) {
        if (aiResponse == null || !Boolean.TRUE.equals(aiResponse.getSuccess())) {
            throw new CustomException(ScenarioErrorCode.SCENARIO_GENERATION_FAILED);
        }

        if (aiResponse.getData() == null || aiResponse.getData().getLevels() == null) {
            throw new CustomException(ScenarioErrorCode.SCENARIO_GENERATION_FAILED);
        }

        if (aiResponse.getData().getLevels().size() != 3) {
            throw new CustomException(ScenarioErrorCode.SCENARIO_GENERATION_FAILED);
        }

        boolean hasInvalidStepCount = aiResponse.getData().getLevels().stream()
                .anyMatch(level -> level.getSteps() == null || level.getSteps().size() != 3);

        if (hasInvalidStepCount) {
            throw new CustomException(ScenarioErrorCode.SCENARIO_GENERATION_FAILED);
        }
    }
}