package com.kwcapstone.server.domain.mysentence.exception.code;

import com.kwcapstone.server.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MySentenceErrorCode implements BaseCode {

    // 4xx
    EMPTY_SENTENCE_CONTENT(HttpStatus.BAD_REQUEST, "WARMUP_400_1", "문장 내용이 비어있습니다."),
    SENTENCE_LENGTH_EXCEEDED(HttpStatus.BAD_REQUEST, "WARMUP_400_2", "문장 길이를 초과했습니다."),
    AUDIO_FILE_REQUIRED(HttpStatus.BAD_REQUEST, "WARMUP400_3", "오디오 파일이 없습니다."),
    UNSUPPORTED_AUDIO_FORMAT(HttpStatus.BAD_REQUEST, "WARMUP400_4", "지원하지 않는 오디오 형식입니다."),
    AUDIO_TOO_SHORT(HttpStatus.BAD_REQUEST, "WARMUP400_5", "오디오 길이가 너무 짧습니다."),
    MY_SENTENCE_FORBIDDEN(HttpStatus.FORBIDDEN, "WARMUP_403", "본인의 문장이 아니어서 삭제할 수 없습니다."),
    MY_SENTENCE_NOT_FOUND(HttpStatus.NOT_FOUND, "WARMUP_404_1", "존재하지 않거나 이미 삭제된 문장입니다."),
    RECENT_USER_AUDIO_NOT_FOUND(HttpStatus.NOT_FOUND, "WARMUP_404_2", "최근 녹음 음성이 없습니다."),

    // 5xx
    TTS_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "WARMUP_500", "TTS 생성에 실패했습니다."),
    PRONUNCIATION_ANALYSIS_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "WARMUP500_2", "발음 분석에 실패했습니다.")
    ;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
