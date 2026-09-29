package com.kwcapstone.server.domain.audio.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PresignedUploadResDTO {
    private String key;
    private String uploadUrl;
    private String contentType;
    private long expiresInSeconds;
}
