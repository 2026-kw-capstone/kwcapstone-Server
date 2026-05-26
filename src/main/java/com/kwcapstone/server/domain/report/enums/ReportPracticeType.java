package com.kwcapstone.server.domain.report.enums;

import com.kwcapstone.server.global.apiPayload.exception.CustomException;
import com.kwcapstone.server.global.apiPayload.response.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ReportPracticeType {
    MY_SENTENCE("문장 노트"),
    BASIC("기초 발성"),
    SCENARIO("시나리오")
    ;

    private final String typeLabel;

    // 문자열 -> enum 변환
    public static ReportPracticeType from(String value) {
        if (value == null || value.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST));
    }
}
