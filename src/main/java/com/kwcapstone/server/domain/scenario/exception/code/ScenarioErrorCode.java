package com.kwcapstone.server.domain.scenario.exception.code;

import com.kwcapstone.server.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ScenarioErrorCode implements BaseCode {

    // 4xx
    EMPTY_SCENARIO_TITLE(HttpStatus.BAD_REQUEST, "SCENAIRO_400_1", "시나리오 제목은 필수입니다."),
    EMPTY_SCENARIO_DESCRIPTION(HttpStatus.BAD_REQUEST, "SCENAIRO_400_2", "시나리오 상세 설명은 필수입니다."),
    INVALID_LEVEL(HttpStatus.BAD_REQUEST, "SCENAIRO_400_3", "유효하지 않은 레벨입니다."),
    INVALID_STEP(HttpStatus.BAD_REQUEST, "SCENARIO_400_4", "유효하지 않은 단계입니다."),
    AUDIO_FILE_REQUIRED(HttpStatus.BAD_REQUEST, "SCENARIO_400_5", "음성 파일은 필수입니다."),
    UNSUPPORTED_AUDIO_FORMAT(HttpStatus.BAD_REQUEST, "SCENARIO_400_6", "지원하지 않는 음성 파일 형식입니다."),
    SCENARIO_FORBIDDEN(HttpStatus.FORBIDDEN, "SCENAIRO_403", "해당 시나리오에 접근할 수 없습니다."),
    SCENARIO_NOT_FOUND(HttpStatus.NOT_FOUND, "SCENARIO_404_1", "시나리오를 찾을 수 없습니다."),
    SCENARIO_STEP_NOT_FOUND(HttpStatus.NOT_FOUND, "SCENARIO_404_2", "대화 단계를 찾을 수 없습니다."),
    USER_AUDIO_NOT_FOUND(HttpStatus.NOT_FOUND, "SCENARIO_404_3", "저장된 음성 파일을 찾을 수 없습니다."),

    // 5xx
    SCENARIO_GENERATION_FAILED(HttpStatus.BAD_GATEWAY, "SCENAIRO_502_1", "AI 서버 시나리오 생성에 실패했습니다."),
    SCENARIO_ANALYSIS_FAILED(HttpStatus.BAD_GATEWAY, "SCENARIO_502_2", "AI 서버 음성 분석에 실패했습니다.")
    ;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
