package com.kwcapstone.server.domain.basicpronunciation.exception.code;

import com.kwcapstone.server.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BasicPronunciationErrorCode implements BaseCode {
    // BASIC_PRONUNCIATION 4XX (클라이언트 오류)
    INVALID_TARGET_VOWEL(HttpStatus.BAD_REQUEST, "BASIC_PRONUNCIATION400", "지원하지 않는 목표 단모음입니다."),

    // BASIC_PRONUNCIATION 5XX (서버 오류)
    EMPTY_FEEDBACK(HttpStatus.INTERNAL_SERVER_ERROR, "BASIC_PRONUNCIATION500_1", "AI 서버가 피드백을 반환하지 않았습니다."),
    EMPTY_TTS_AUDIO(HttpStatus.INTERNAL_SERVER_ERROR, "BASIC_PRONUNCIATION500_2", "AI 서버가 모범 발음 음성을 반환하지 않았습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
