package com.kwcapstone.server.global.util;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
    // refreshToken 쿠키를 생성해서 응답에 추가
    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken, long maxAgeSeconds) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true) // JS에서 접근 불가
                .secure(false) // 개발 환경에서는 HTTP도 허용
                .path("/") // 전체 경로에서 쿠키 사용 가능
                .sameSite("Lax") // 같은 사이트 요청에서는 정상 전송
                .maxAge(maxAgeSeconds) // 쿠키 만료 시간 설정
                .build();

        // ex) Set-Cookie: refreshToken=...; Path=/; HttpOnly; SameSite=Lax; Max-Age=...
        response.addHeader("Set-Cookie", cookie.toString());
    }

    // 브라우저에 저장된 refreshToken 쿠키 삭제 (로그아웃)
    public void clearRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
