package com.kwcapstone.server.global.storage.audio;

public record PresignedUploadUrlResult(
        String key,
        String uploadUrl,
        String contentType,
        long expiresInSeconds
) {
}
