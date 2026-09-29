package com.kwcapstone.server.global.storage.audio;

public record StoredAudioObjectMetadata(
        String key,
        long contentLength,
        String contentType
) {
}
