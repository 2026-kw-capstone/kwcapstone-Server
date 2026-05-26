package com.kwcapstone.server.domain.report.enums;

import com.kwcapstone.server.global.apiPayload.exception.CustomException;
import com.kwcapstone.server.global.apiPayload.response.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ReportPeriod {
    WEEK("이번 주", "저번 주"),
    MONTH("이번 달", "저번 달")
    ;

    private final String currentLabel;
    private final String previousLabel;

    // 문자열 -> enum 변환
    public static ReportPeriod from(String value) {
        if (value == null || value.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        return Arrays.stream(values())
                .filter(period -> period.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST));
    }
}
