package com.kwcapstone.server.domain.audio.service;

import com.kwcapstone.server.domain.audio.dto.request.CompleteAudioUploadReqDTO;
import com.kwcapstone.server.domain.audio.dto.request.PresignedUploadReqDTO;
import com.kwcapstone.server.domain.audio.dto.response.CompleteAudioUploadResDTO;
import com.kwcapstone.server.domain.audio.dto.response.LegacyUploadBenchmarkResDTO;
import com.kwcapstone.server.domain.audio.dto.response.PresignedUploadResDTO;
import com.kwcapstone.server.global.apiPayload.exception.CustomException;
import com.kwcapstone.server.global.apiPayload.response.ErrorCode;
import com.kwcapstone.server.global.security.SecurityUtil;
import com.kwcapstone.server.global.storage.audio.AudioFilePolicy;
import com.kwcapstone.server.global.storage.audio.AudioStorageService;
import com.kwcapstone.server.global.storage.audio.PresignedUploadUrlResult;
import com.kwcapstone.server.global.storage.audio.StoredAudioObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AudioUploadServiceImpl implements AudioUploadService {

    private static final String DIRECT_UPLOAD_PREFIX = "uploads/member";

    private final AudioStorageService audioStorageService;
    private final AudioFilePolicy audioFilePolicy;

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

    @Override
    public CompleteAudioUploadResDTO completeUpload(CompleteAudioUploadReqDTO request) {
        Long memberId = SecurityUtil.getCurrentMemberId();

        String key = request.getKey();

        String expectedPrefix = "audio/uploads/member/" + memberId + "/";

        // 다른 사용자의 Object Key 접근 방지
        if (!key.startsWith(expectedPrefix)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        // 실제 S3 객체 정보 조회
        StoredAudioObjectMetadata metadata = audioStorageService.getObjectMetadata(key);

        try {
            // 클라이언트가 주장한 값이 아닌 실제 S3 메타데이터로 검증
            audioFilePolicy.validate(
                    key,
                    metadata.contentType(),
                    metadata.contentLength()
            );

        } catch (CustomException e) {

            // 잘못 올라간 객체는 삭제
            audioStorageService.delete(key);

            throw e;
        }

        return new CompleteAudioUploadResDTO(
                metadata.key(),
                metadata.contentLength(),
                metadata.contentType()
        );
    }

    @Override
    public LegacyUploadBenchmarkResDTO uploadLegacyForBenchmark(MultipartFile file) {
        Long memberId = SecurityUtil.getCurrentMemberId();

        String keyPrefix = "benchmark/legacy/member/" + memberId;

        String fileBaseName = UUID.randomUUID().toString();

        String key = audioStorageService.upload(
                keyPrefix,
                fileBaseName,
                file
        );

        return new LegacyUploadBenchmarkResDTO(
                key,
                file.getSize(),
                file.getContentType()
        );
    }
}
