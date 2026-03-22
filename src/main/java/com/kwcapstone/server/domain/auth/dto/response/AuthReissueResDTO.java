package com.kwcapstone.server.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthReissueResDTO { // Access Token 재발급 응답 DTO
    private String accessToken;
}
