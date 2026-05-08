package com.kwcapstone.server.domain.mysentence.exception.code;

import com.kwcapstone.server.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MySentenceErrorCode implements BaseCode {

    EMPTY_SENTENCE_CONTENT(HttpStatus.BAD_REQUEST, "WARMUP_400_1", "문장 내용이 비어있습니다."),
    SENTENCE_LENGTH_EXCEEDED(HttpStatus.BAD_REQUEST, "WARMUP_400_2", "문장 길이를 초과했습니다.")
    ;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
