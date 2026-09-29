package com.kwcapstone.server.domain.audio.service;

import com.kwcapstone.server.domain.audio.dto.request.PresignedUploadReqDTO;
import com.kwcapstone.server.domain.audio.dto.response.PresignedUploadResDTO;
import com.kwcapstone.server.global.security.SecurityUtil;
import com.kwcapstone.server.global.storage.audio.AudioStorageService;
import com.kwcapstone.server.global.storage.audio.PresignedUploadUrlResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AudioUploadServiceImpl implements AudioUploadService {

    private static final String DIRECT_UPLOAD_PREFIX = "uploads/member";

    private final AudioStorageService audioStorageService;

    @Override
    public PresignedUploadResDTO generatePresignedUploadUrl(PresignedUploadReqDTO request) {
        Long memberId = SecurityUtil.getCurrentMemberId();

        String keyPrefix = DIRECT_UPLOAD_PREFIX + "/" + memberId;

        String fileBaseName = UUID.randomUUID().toString();

        PresignedUploadUrlResult result = audioStorageService.generatePresignedPutUrl(
                keyPrefix,
                fileBaseName,
                request.getFileName(),
                request.getContentType(),
                request.getFileSize()
        );

        return new PresignedUploadResDTO(
                result.key(),
                result.uploadUrl(),
                result.contentType(),
                result.expiresInSeconds()
        );
    }
}
