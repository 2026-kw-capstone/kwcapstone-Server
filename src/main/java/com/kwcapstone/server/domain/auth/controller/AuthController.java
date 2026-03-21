package com.kwcapstone.server.domain.auth.controller;

import com.kwcapstone.server.domain.auth.dto.request.AuthLoginReqDTO;
import com.kwcapstone.server.domain.auth.dto.request.AuthSignUpReqDTO;
import com.kwcapstone.server.domain.auth.dto.response.AuthLoginResDTO;
import com.kwcapstone.server.domain.auth.dto.response.AuthSignUpResDTO;
import com.kwcapstone.server.domain.auth.service.AuthService;
import com.kwcapstone.server.global.apiPayload.response.ApiResponse;
import com.kwcapstone.server.global.apiPayload.response.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

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
            @RequestBody @Valid AuthLoginReqDTO request
    ) {
        AuthLoginResDTO result = authService.login(request);

        return ApiResponse.onSuccess(result, SuccessCode.OK);
    }
}
