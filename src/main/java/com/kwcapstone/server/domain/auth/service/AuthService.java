package com.kwcapstone.server.domain.auth.service;

import com.kwcapstone.server.domain.auth.dto.request.AuthLoginReqDTO;
import com.kwcapstone.server.domain.auth.dto.request.AuthSignUpReqDTO;
import com.kwcapstone.server.domain.auth.dto.response.AuthLoginResDTO;
import com.kwcapstone.server.domain.auth.dto.response.AuthSignUpResDTO;

public interface AuthService {
    AuthSignUpResDTO signup(AuthSignUpReqDTO request);
    LoginTokens login(AuthLoginReqDTO request);
    ReissueTokens reissue(String refreshToken);
    void logout();

    record LoginTokens(
            AuthLoginResDTO body,
            String refreshToken,
            long refreshMaxAgeSeconds
    ) {}

    record ReissueTokens(
            String accessToken,
            String refreshToken,
            long refreshMaxAgeSeconds
    ) {}
}
