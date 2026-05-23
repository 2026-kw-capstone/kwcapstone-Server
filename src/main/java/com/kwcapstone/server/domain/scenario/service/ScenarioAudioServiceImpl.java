package com.kwcapstone.server.domain.scenario.service;

import com.kwcapstone.server.domain.member.entity.Member;
import com.kwcapstone.server.domain.member.repository.MemberRepository;
import com.kwcapstone.server.domain.scenario.client.ScenarioAiClient;
import com.kwcapstone.server.domain.scenario.client.dto.request.ScenarioPracticeAiReqDTO;
import com.kwcapstone.server.domain.scenario.client.dto.response.ScenarioPracticeAiResDTO;
import com.kwcapstone.server.domain.scenario.converter.ScenarioConverter;
import com.kwcapstone.server.domain.scenario.dto.request.ScenarioAnswerAnalyzeReqDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioAnswerAnalyzeResDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioUserAudioResDTO;
import com.kwcapstone.server.domain.scenario.entity.Scenario;
import com.kwcapstone.server.domain.scenario.entity.ScenarioAnalysisResult;
import com.kwcapstone.server.domain.scenario.entity.ScenarioLevel;
import com.kwcapstone.server.domain.scenario.entity.ScenarioStep;
import com.kwcapstone.server.domain.scenario.exception.code.ScenarioErrorCode;
import com.kwcapstone.server.domain.scenario.repository.ScenarioAnalysisResultRepository;
import com.kwcapstone.server.domain.scenario.repository.ScenarioLevelRepository;
import com.kwcapstone.server.domain.scenario.repository.ScenarioRepository;
import com.kwcapstone.server.domain.scenario.repository.ScenarioStepRepository;
import com.kwcapstone.server.global.apiPayload.exception.CustomException;
import com.kwcapstone.server.global.apiPayload.response.ErrorCode;
import com.kwcapstone.server.global.security.SecurityUtil;
import com.kwcapstone.server.global.storage.audio.AudioStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ScenarioAudioServiceImpl implements ScenarioAudioService {

    private static final String MEMBER_AUDIO_PREFIX = "conversations/scenario/member";
    private static final long PRESIGNED_EXPIRES_IN = 600L;

    private final ScenarioRepository scenarioRepository;
    private final ScenarioLevelRepository scenarioLevelRepository;
    private final ScenarioStepRepository scenarioStepRepository;
    private final ScenarioAnalysisResultRepository scenarioAnalysisResultRepository;
    private final MemberRepository memberRepository;

    private final ScenarioAiClient scenarioAiClient;
    private final AudioStorageService audioStorageService;
    private final ObjectMapper objectMapper;

    @Override
    public ScenarioAnswerAnalyzeResDTO analyzeScenarioAnswer(
            ScenarioAnswerAnalyzeReqDTO request
    ) {
        validateLevel(request.getLevel());
        validateStep(request.getStepNo());

        Long memberId = SecurityUtil.getCurrentMemberId();

        // 중복 요청 확인
        ScenarioAnalysisResult duplicated =
                scenarioAnalysisResultRepository
                        .findByMemberIdAndClientRequestId(
                                memberId,
                                request.getClientRequestId()
                        )
                        .orElse(null);

        if (duplicated != null) {
            return toAnswerAnalyzeResponseWithSavedWordAnalysis(duplicated);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        Scenario scenario = scenarioRepository.findById(request.getScenarioId())
                .orElseThrow(() -> new CustomException(ScenarioErrorCode.SCENARIO_NOT_FOUND));

        if (!scenario.getMember().getId().equals(memberId)) {
            throw new CustomException(ScenarioErrorCode.SCENARIO_FORBIDDEN);
        }

        ScenarioLevel scenarioLevel = scenarioLevelRepository
                .findByScenarioIdAndLevelNo(
                        request.getScenarioId(),
                        request.getLevel()
                )
                .orElseThrow(() -> new CustomException(ScenarioErrorCode.SCENARIO_STEP_NOT_FOUND));

        ScenarioStep scenarioStep = scenarioStepRepository
                .findByScenarioLevelIdAndStepNo(
                        scenarioLevel.getId(),
                        request.getStepNo()
                )
                .orElseThrow(() -> new CustomException(ScenarioErrorCode.SCENARIO_STEP_NOT_FOUND));

        // 사용자 녹음 파일 S3 업로드
        String memberAudioKey = audioStorageService.upload(
                buildMemberAudioKeyPrefix(memberId),
                request.getClientRequestId(),
                request.getVoiceFile()
        );

        try {
            String memberVoiceUrlForAi =
                    audioStorageService.generatePresignedGetUrl(memberAudioKey);

            ScenarioPracticeAiResDTO aiResponse =
                    scenarioAiClient.practiceScenario(
                            new ScenarioPracticeAiReqDTO(
                                    memberVoiceUrlForAi,
                                    scenarioLevel.getLevelTitle(),
                                    scenarioStep.getStepName(),
                                    scenarioStep.getAssistantMessage(),
                                    scenarioStep.getUserIntent()
                            )
                    );

            validateAiResponse(aiResponse);

            List<ScenarioAnswerAnalyzeResDTO.WordAnalysis> wordAnalysis =
                    ScenarioConverter.toWordAnalysisResponse(aiResponse);

            String wordAnalysisJson =
                    objectMapper.writeValueAsString(wordAnalysis);

            ScenarioAnalysisResult analysisResult =
                    ScenarioConverter.toScenarioAnalysisResult(
                            scenarioStep,
                            member,
                            request.getClientRequestId(),
                            memberAudioKey,
                            wordAnalysisJson,
                            aiResponse
                    );

            ScenarioAnalysisResult saved =
                    scenarioAnalysisResultRepository.save(analysisResult);

            return toAnswerAnalyzeResponse(
                    saved,
                    scenario,
                    request.getLevel(),
                    request.getStepNo(),
                    wordAnalysis
            );

        } catch (Exception e) {
            audioStorageService.delete(memberAudioKey);

            if (e instanceof CustomException customException) {
                throw customException;
            }

            throw new CustomException(ScenarioErrorCode.SCENARIO_ANALYSIS_FAILED);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ScenarioUserAudioResDTO getScenarioUserAudio(
            Long scenarioId,
            Integer level,
            Integer stepNo
    ) {
        validateLevel(level);
        validateStep(stepNo);

        Long memberId = SecurityUtil.getCurrentMemberId();

        Scenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new CustomException(ScenarioErrorCode.SCENARIO_NOT_FOUND));

        if (!scenario.getMember().getId().equals(memberId)) {
            throw new CustomException(ScenarioErrorCode.SCENARIO_FORBIDDEN);
        }

        ScenarioLevel scenarioLevel = scenarioLevelRepository
                .findByScenarioIdAndLevelNo(scenarioId, level)
                .orElseThrow(() -> new CustomException(ScenarioErrorCode.SCENARIO_STEP_NOT_FOUND));

        ScenarioStep scenarioStep = scenarioStepRepository
                .findByScenarioLevelIdAndStepNo(scenarioLevel.getId(), stepNo)
                .orElseThrow(() -> new CustomException(ScenarioErrorCode.SCENARIO_STEP_NOT_FOUND));

        ScenarioAnalysisResult analysisResult = scenarioAnalysisResultRepository
                .findTopByScenarioStepIdAndMemberIdOrderByCreatedAtDesc(
                        scenarioStep.getId(),
                        memberId
                )
                .orElseThrow(() -> new CustomException(ScenarioErrorCode.USER_AUDIO_NOT_FOUND));

        if (!StringUtils.hasText(analysisResult.getUserAudioKey())) {
            throw new CustomException(ScenarioErrorCode.USER_AUDIO_NOT_FOUND);
        }

        String userAudioUrl =
                audioStorageService.generatePresignedGetUrl(analysisResult.getUserAudioKey());

        return new ScenarioUserAudioResDTO(
                analysisResult.getId(),
                scenario.getId(),
                level,
                stepNo,
                userAudioUrl,
                PRESIGNED_EXPIRES_IN
        );
    }

    private ScenarioAnswerAnalyzeResDTO toAnswerAnalyzeResponse(
            ScenarioAnalysisResult analysisResult,
            Scenario scenario,
            Integer level,
            Integer stepNo,
            List<ScenarioAnswerAnalyzeResDTO.WordAnalysis> wordAnalysis
    ) {
        boolean isLastStep = stepNo.equals(3);
        Integer nextStepNo = isLastStep ? null : stepNo + 1;

        return new ScenarioAnswerAnalyzeResDTO(
                analysisResult.getId(),
                scenario.getId(),
                level,
                stepNo,
                analysisResult.getPronunciationScore(),
                analysisResult.getMeaningDeliveryScore(),
                analysisResult.getSpeechRateScore(),
                analysisResult.getSilenceRatio(),
                analysisResult.getAiFeedback(),
                isLastStep,
                nextStepNo,
                wordAnalysis
        );
    }

    // 중복 요청 시 저장된 wordAnalysis JSON으로 응답 생성
    private ScenarioAnswerAnalyzeResDTO toAnswerAnalyzeResponseWithSavedWordAnalysis(
            ScenarioAnalysisResult analysisResult
    ) {
        try {
            List<ScenarioAnswerAnalyzeResDTO.WordAnalysis> wordAnalysis =
                    objectMapper.readValue(
                            analysisResult.getWordAnalysisJson(),
                            new TypeReference<List<ScenarioAnswerAnalyzeResDTO.WordAnalysis>>() {
                            }
                    );

            ScenarioStep scenarioStep = analysisResult.getScenarioStep();
            ScenarioLevel scenarioLevel = scenarioStep.getScenarioLevel();
            Scenario scenario = scenarioLevel.getScenario();

            return toAnswerAnalyzeResponse(
                    analysisResult,
                    scenario,
                    scenarioLevel.getLevelNo(),
                    scenarioStep.getStepNo(),
                    wordAnalysis
            );

        } catch (Exception e) {
            throw new CustomException(ScenarioErrorCode.SCENARIO_ANALYSIS_FAILED);
        }
    }

    // level 값이 1~3 범위 내에 있는지 검증
    private void validateLevel(Integer level) {
        if (level == null || level < 1 || level > 3) {
            throw new CustomException(ScenarioErrorCode.INVALID_LEVEL);
        }
    }

    // stepNo 값이 1~3 범위 내에 있는지 검증
    private void validateStep(Integer stepNo) {
        if (stepNo == null || stepNo < 1 || stepNo > 3) {
            throw new CustomException(ScenarioErrorCode.INVALID_STEP);
        }
    }

    private void validateAiResponse(ScenarioPracticeAiResDTO aiResponse) {
        if (aiResponse == null || !Boolean.TRUE.equals(aiResponse.getSuccess())) {
            throw new CustomException(ScenarioErrorCode.SCENARIO_ANALYSIS_FAILED);
        }
    }

    // 사용자 음성 저장 경로 prefix 생성 메서드
    private String buildMemberAudioKeyPrefix(Long memberId) {
        return MEMBER_AUDIO_PREFIX + "/" + memberId;
    }
}