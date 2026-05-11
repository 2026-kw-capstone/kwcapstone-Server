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
    SCENARIO_FORBIDDEN(HttpStatus.FORBIDDEN, "SCENAIRO_403", "해당 시나리오에 접근할 수 없습니다."),
    SCENARIO_NOT_FOUND(HttpStatus.NOT_FOUND, "SCENARIO_404", "시나리오를 찾을 수 없습니다."),


    // 5xx
    SCENARIO_GENERATION_FAILED(HttpStatus.BAD_GATEWAY, "SCENAIRO_502", "AI 서버 시나리오 생성에 실패했습니다.")
    ;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
