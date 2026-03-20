package com.kwcapstone.server.global.apiPayload.handler;

import com.kwcapstone.server.global.apiPayload.exception.CustomException;
import com.kwcapstone.server.global.apiPayload.response.ApiResponse;
import com.kwcapstone.server.global.apiPayload.response.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
public class ExceptionAdvice extends ResponseEntityExceptionHandler {
    // @RequestParam, @PathVariable 등 Bean Validation 실패 시
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException e, WebRequest request) {
        String message = e.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .findFirst()
                .orElse("잘못된 요청입니다.");

        ApiResponse<Object> body = ApiResponse.onFailure(message, ErrorCode.INVALID_REQUEST);

        return handleExceptionInternal(e, body, new HttpHeaders(), ErrorCode.INVALID_REQUEST.getHttpStatus(), request);
    }

    // @Valid + @RequestBody DTO 검증 실패 시
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();

        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            String field = fieldError.getField(); // ex) field = "email"
            String message = Optional.ofNullable(fieldError.getDefaultMessage()).orElse(""); // ex) message = "이메일 형식이 아닙니다.", message가 null이면 빈 문자열로 대체
            errors.merge(field, message, (a, b) -> a + ", " + b); // 같은 필드에 여러 에러가 있을 수 있음. ex) "password": "비어있음, 길이 부족"
        });

        ApiResponse<Object> body = ApiResponse.onFailure(errors, ErrorCode.VALIDATION_ERROR);

        return handleExceptionInternal(e, body, headers, ErrorCode.VALIDATION_ERROR.getHttpStatus(), request);
    }

    // JSON 파싱 실패(타입 불일치) 시
    @ExceptionHandler({InvalidFormatException.class})
    public ResponseEntity<ApiResponse<Object>> handleInvalidFormat(InvalidFormatException e) {
        return ResponseEntity.badRequest().body(
                ApiResponse.onFailure(null, ErrorCode.INVALID_REQUEST) // 400
        );
    }

    // 도메인 CustomException 발생 시
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> handleCustomException(CustomException e, HttpServletRequest request) {
        ApiResponse<Object> body = ApiResponse.onFailure(null, e.getErrorCode());
        WebRequest webRequest = new ServletWebRequest(request);

        return handleExceptionInternal(e, body, new HttpHeaders(), e.getErrorCode().getHttpStatus(), webRequest);
    }

    // 처리되지 않은 모든 예외 -> 500 응답 (예외로 인한 서버 다운 방지)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnknownException(Exception e, WebRequest request) {
        log.error("Unhandled exception", e); // printStackTrace() 지양
        ApiResponse<Object> body = ApiResponse.onFailure(null, ErrorCode.INTERNAL_SERVER_ERROR); // 내부 메시지 노출 X

        return handleExceptionInternal(e, body, new HttpHeaders(), ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus(), request);
    }
}
