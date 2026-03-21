package com.kwcapstone.server.domain.auth.service;

import com.kwcapstone.server.domain.auth.dto.request.AuthLoginReqDTO;
import com.kwcapstone.server.domain.auth.dto.request.AuthSignUpReqDTO;
import com.kwcapstone.server.domain.auth.dto.response.AuthLoginResDTO;
import com.kwcapstone.server.domain.auth.dto.response.AuthSignUpResDTO;

public interface AuthService {
    AuthSignUpResDTO signup(AuthSignUpReqDTO request);
    AuthLoginResDTO login(AuthLoginReqDTO request);
}
