package com.kwcapstone.server.domain.auth.controller;

import com.kwcapstone.server.domain.auth.dto.request.AuthLoginReqDTO;
import com.kwcapstone.server.domain.auth.dto.request.AuthSignUpReqDTO;
import com.kwcapstone.server.domain.auth.dto.response.AuthLoginResDTO;
import com.kwcapstone.server.domain.auth.dto.response.AuthReissueResDTO;
import com.kwcapstone.server.domain.auth.dto.response.AuthSignUpResDTO;
import com.kwcapstone.server.domain.auth.service.AuthService;
import com.kwcapstone.server.global.apiPayload.response.ApiResponse;
import com.kwcapstone.server.global.apiPayload.response.SuccessCode;
import com.kwcapstone.server.global.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final CookieUtil cookieUtil;

    // 회원가입
    @PostMapping("/signup")
    public ApiResponse<AuthSignUpResDTO> signup(
            @RequestBody @Valid AuthSignUpReqDTO request
    ) {
        AuthSignUpResDTO result = authService.signup(request);

        return ApiResponse.onSuccess(result, SuccessCode.CREATED);
    }

    // 로그인
    @PostMapping("/login")
    public ApiResponse<AuthLoginResDTO> login(
            @RequestBody @Valid AuthLoginReqDTO request,
            HttpServletResponse response
    ) {
        AuthService.LoginTokens tokens = authService.login(request);
        cookieUtil.addRefreshTokenCookie(
                response,
                tokens.refreshToken(),
                tokens.refreshMaxAgeSeconds()
        );

        return ApiResponse.onSuccess(tokens.body(), SuccessCode.OK);
    }

    /**
     * Access Token 재발급
     * 1. 클라이언트가 POST /auth/reissue 호출
     * 2. 브라우저가 refreshToken 쿠키를 자동으로 전송
     * 3. Controller가 @CookieValue로 refreshToken 추출
     * 4. Service가 새 accessToken, refreshToken 발급
     * 5. 새 refreshToken은 쿠키로 재설정 (Refresh Token Rotation)
     * 6. 새 accessToken은 JSON 응답으로 반환
     */
    @PostMapping("/reissue")
    public ApiResponse<AuthReissueResDTO> reissue(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        AuthService.ReissueTokens tokens = authService.reissue(refreshToken);
        cookieUtil.addRefreshTokenCookie( // Refresh Token Rotation
                response,
                tokens.refreshToken(),
                tokens.refreshMaxAgeSeconds()
        );

        return ApiResponse.onSuccess(new AuthReissueResDTO(tokens.accessToken()), SuccessCode.OK);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletResponse response) {
        authService.logout();
        cookieUtil.clearRefreshTokenCookie(response);

        return ApiResponse.onSuccess(null, SuccessCode.OK);
    }
}
