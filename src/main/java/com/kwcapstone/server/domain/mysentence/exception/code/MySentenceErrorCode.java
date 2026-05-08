package com.kwcapstone.server.domain.mysentence.exception.code;

import com.kwcapstone.server.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MySentenceErrorCode implements BaseCode {

    EMPTY_SENTENCE_CONTENT(HttpStatus.BAD_REQUEST, "WARMUP_400_1", "문장 내용이 비어있습니다."),
    SENTENCE_LENGTH_EXCEEDED(HttpStatus.BAD_REQUEST, "WARMUP_400_2", "문장 길이를 초과했습니다."),
    MY_SENTENCE_NOT_FOUND(HttpStatus.NOT_FOUND, "WARMUP404", "존재하지 않거나 이미 삭제된 문장입니다."),
    MY_SENTENCE_FORBIDDEN(HttpStatus.FORBIDDEN, "WARMUP403", "본인의 문장이 아니어서 삭제할 수 없습니다."),
    TTS_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "WARMUP500_1", "TTS 생성에 실패했습니다.")
    ;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
