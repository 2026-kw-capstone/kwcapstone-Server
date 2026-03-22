package com.kwcapstone.server.global.security;

import com.kwcapstone.server.global.apiPayload.exception.CustomException;
import com.kwcapstone.server.global.apiPayload.response.ErrorCode;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
    // 현재 로그인한 사용자 정보 조회
    public static Long getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 객체 자체가 없거나 인증이 안 된 경우
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // 익명 사용자(로그인 하지 않은 상태)일 경우
        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // 현재 로그인한 사용자 정보
        Object principal = authentication.getPrincipal();

        // JWT에서 추출한 memberId 반환
        if (principal instanceof AuthPrincipal authPrincipal) {
            return authPrincipal.getMemberId();
        }

        throw new CustomException(ErrorCode.UNAUTHORIZED);
    }
}
