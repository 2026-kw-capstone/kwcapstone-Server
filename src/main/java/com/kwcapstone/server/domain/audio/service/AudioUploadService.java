package com.kwcapstone.server.domain.audio.service;

import com.kwcapstone.server.domain.audio.dto.request.PresignedUploadReqDTO;
import com.kwcapstone.server.domain.audio.dto.response.PresignedUploadResDTO;

public interface AudioUploadService {
    PresignedUploadResDTO generatePresignedUploadUrl(PresignedUploadReqDTO request);
}
