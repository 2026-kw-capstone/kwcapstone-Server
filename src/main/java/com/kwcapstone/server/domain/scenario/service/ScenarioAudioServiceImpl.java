package com.kwcapstone.server.domain.scenario.service;

import com.kwcapstone.server.domain.member.entity.Member;
import com.kwcapstone.server.domain.member.repository.MemberRepository;
import com.kwcapstone.server.domain.mysentence.exception.code.MySentenceErrorCode;
import com.kwcapstone.server.domain.scenario.client.ScenarioAiClient;
import com.kwcapstone.server.domain.scenario.dto.request.ScenarioPracticeAiReqDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioAnswerAnalyzeResDTO;
import com.kwcapstone.server.domain.scenario.dto.response.ScenarioPracticeAiResDTO;
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
import com.kwcapstone.server.global.storage.audio.S3AudioStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Transactional
public class ScenarioAudioServiceImpl implements ScenarioAudioService {

    private final ScenarioRepository scenarioRepository;
    private final ScenarioLevelRepository scenarioLevelRepository;
    private final ScenarioStepRepository scenarioStepRepository;
    private final ScenarioAnalysisResultRepository scenarioAnalysisResultRepository;
    private final MemberRepository memberRepository;

    private final ScenarioAiClient scenarioAiClient;
    private final S3AudioStorageService s3AudioStorageService;
    private final ObjectMapper objectMapper;

    @Override
    public ScenarioAnswerAnalyzeResDTO analyzeScenarioAnswer(
            Long scenarioId,
            Integer level,
            Integer stepNo,
            MultipartFile voiceFile
    ) {
        validateLevel(level);
        validateStep(stepNo);
        validateVoiceFile(voiceFile);

        Long memberId = SecurityUtil.getCurrentMemberId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

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

        String userAudioKey = uploadVoiceFile(
                memberId,
                scenarioId,
                level,
                stepNo,
                voiceFile
        );

        String presignedUrl = s3AudioStorageService.generatePresignedGetUrl(userAudioKey);

        ScenarioPracticeAiResDTO aiResponse = scenarioAiClient.practiceScenario(
                new ScenarioPracticeAiReqDTO(
                        presignedUrl,
                        scenarioLevel.getLevelTitle(),
                        scenarioStep.getStepName(),
                        scenarioStep.getAssistantMessage(),
                        scenarioStep.getUserIntent()
                )
        );

        validateAiResponse(aiResponse);

        ScenarioAnalysisResult analysisResult = ScenarioAnalysisResult.builder()
                .scenarioStep(scenarioStep)
                .member(member)
                .userAudioKey(userAudioKey)
                .pronunciationScore(aiResponse.getPronunciationScore())
                .meaningDeliveryScore(aiResponse.getMeaningDeliveryScore())
                .speechRateScore(aiResponse.getVoiceAnalysis().getSpeechRate().getScore())
                .silenceRatio(aiResponse.getVoiceAnalysis().getSilenceRatio().getPausePercent())
                .aiFeedback(aiResponse.getFeedback())
                .wordAnalysisJson(toJson(aiResponse.getWordAnalysis()))
                .build();

        ScenarioAnalysisResult saved = scenarioAnalysisResultRepository.save(analysisResult);

        boolean isLastStep = stepNo.equals(3);
        Integer nextStepNo = isLastStep ? null : stepNo + 1;

        return new ScenarioAnswerAnalyzeResDTO(
                saved.getId(),
                scenario.getId(),
                level,
                stepNo,
                aiResponse.getPronunciationScore(),
                aiResponse.getMeaningDeliveryScore(),
                aiResponse.getVoiceAnalysis().getSpeechRate().getScore(),
                aiResponse.getVoiceAnalysis().getSilenceRatio().getPausePercent(),
                aiResponse.getFeedback(),
                isLastStep,
                nextStepNo,
                aiResponse.getWordAnalysis().stream()
                        .map(word -> new ScenarioAnswerAnalyzeResDTO.WordAnalysis(
                                word.getRefChar(),
                                word.getHypChar(),
                                word.getGrade()
                        ))
                        .toList()
        );
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

        if (analysisResult.getUserAudioKey() == null || analysisResult.getUserAudioKey().isBlank()) {
            throw new CustomException(ScenarioErrorCode.USER_AUDIO_NOT_FOUND);
        }

        String userAudioUrl =
                s3AudioStorageService.generatePresignedGetUrl(analysisResult.getUserAudioKey());

        return new ScenarioUserAudioResDTO(
                analysisResult.getId(),
                scenario.getId(),
                level,
                stepNo,
                userAudioUrl,
                600
        );
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

    // 파일 검증 매서드
    private void validateVoiceFile(MultipartFile voiceFile) {
        if (voiceFile == null || voiceFile.isEmpty()) {
            throw new CustomException(ScenarioErrorCode.AUDIO_FILE_REQUIRED);
        }

        String contentType = voiceFile.getContentType();

        if (contentType == null ||
                !(contentType.equals("audio/mpeg")
                        || contentType.equals("audio/mp3")
                        || contentType.equals("audio/wav")
                        || contentType.equals("audio/webm")
                        || contentType.equals("audio/x-m4a")
                        || contentType.equals("audio/mp4"))) {
            throw new CustomException(MySentenceErrorCode.UNSUPPORTED_AUDIO_FORMAT);
        }
    }

    // AI 서버 응답이 정상이며 필수 분석 결과가 모두 포함되어 있는지 검증
    private void validateAiResponse(ScenarioPracticeAiResDTO aiResponse) {
        if (aiResponse == null || !Boolean.TRUE.equals(aiResponse.getSuccess())) {
            throw new CustomException(ScenarioErrorCode.SCENARIO_ANALYSIS_FAILED);
        }

        if (aiResponse.getVoiceAnalysis() == null
                || aiResponse.getVoiceAnalysis().getSpeechRate() == null
                || aiResponse.getVoiceAnalysis().getSilenceRatio() == null
                || aiResponse.getWordAnalysis() == null) {
            throw new CustomException(ScenarioErrorCode.SCENARIO_ANALYSIS_FAILED);
        }
    }

    // json 변환
    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new CustomException(ScenarioErrorCode.SCENARIO_ANALYSIS_FAILED);
        }
    }

    // 사용자 음성 파일을 S3에 업로드하고 저장된 객체 키를 반환
    private String uploadVoiceFile(
            Long memberId,
            Long scenarioId,
            Integer level,
            Integer stepNo,
            MultipartFile voiceFile
    ) {
        return s3AudioStorageService.upload(
                "conversation/" + memberId,
                "scenario-" + scenarioId
                        + "-level-" + level
                        + "-step-" + stepNo,
                voiceFile
        );
    }
}