package com.kwcapstone.server.domain.conversation.exception.code;

import com.kwcapstone.server.global.apiPayload.response.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ConversationErrorCode implements BaseCode {
    // CONVERSATION 4XX (클라이언트 오류)
    CONVERSATION_FORBIDDEN(HttpStatus.FORBIDDEN, "CONVERSATION403", "접근 권한이 없는 채팅방입니다."),
    CONVERSATION_NOT_FOUND(HttpStatus.NOT_FOUND, "CONVERSATION404", "존재하지 않는 채팅방입니다."),

    // MESSAGE 4XX (클라이언트 오류)
    MESSAGE_VOICE_NOT_FOUND(HttpStatus.BAD_REQUEST, "MESSAGE400", "음성 파일이 없는 메시지입니다."),
    MESSAGE_FORBIDDEN(HttpStatus.FORBIDDEN, "MESSAGE403", "접근 권한이 없는 메시지입니다."),
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "MESSAGE404", "존재하지 않는 메시지입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
