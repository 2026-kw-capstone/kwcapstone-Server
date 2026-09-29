package com.kwcapstone.server.domain.audio.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CompleteAudioUploadResDTO {
    private String key;
    private long fileSize;
    private String contentType;
}
