package com.kwcapstone.server.domain.member.exception.code;

import com.kwcapstone.server.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements BaseCode {
    // MEMBER 4XX (클라이언트 오류)
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER404", "존재하지 않는 사용자입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}