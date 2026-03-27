package com.kwcapstone.server.global.aws.s3;

import com.kwcapstone.server.global.apiPayload.exception.CustomException;
import com.kwcapstone.server.global.apiPayload.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.Locale;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Uploader {
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.voice-path}")
    private String voicePath;

    @Value("${cloud.aws.s3.presigned-expire-seconds}")
    private long presignedExpireSeconds;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "audio/webm",
            "video/webm",
            "audio/mp4",
            "audio/m4a",
            "audio/mpeg",
            "audio/mp3",
            "audio/x-m4a"
    );

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".webm",
            ".mp4",
            ".m4a",
            ".mp3"
    );

    // S3 업로드 후 public URL이 아니라 object key를 반환
    public String uploadVoiceFile(Long memberId, String clientRequestId, MultipartFile file) {
        // 파일 검증
        validateVoiceFile(file);

        // Content-Type 정규화, ex) audio/webm;codecs=opus -> audio/webm
        String normalizedContentType = normalizeContentType(file.getContentType());
        // 확장자 추출
        String extension = resolveExtension(file, normalizedContentType); // ex) abc.webm -> .webm
        // S3 key(=S3 파일 경로) 생성
        String key = generateVoiceKey(memberId, clientRequestId, extension);

        log.info("S3 upload start. bucket={}, key={}, originalFilename={}, contentType={}, normalizedContentType={}, size={}",
                bucket, key, file.getOriginalFilename(), file.getContentType(), normalizedContentType, file.getSize());

        try {
            // 업로드 요청 객체 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(normalizedContentType != null ? normalizedContentType : file.getContentType())
                    .build();

            // S3 업로드
            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            log.info("S3 upload success. bucket={}, key={}", bucket, key);

            return key;
        } catch (S3Exception e) {
            log.error("S3Exception during upload. bucket={}, key={}, statusCode={}, errorCode={}, errorMessage={}",
                    bucket,
                    key,
                    e.statusCode(),
                    e.awsErrorDetails() != null ? e.awsErrorDetails().errorCode() : null,
                    e.awsErrorDetails() != null ? e.awsErrorDetails().errorMessage() : e.getMessage(),
                    e
            );

            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
        } catch (SdkClientException e) {
            log.error("SdkClientException during upload. bucket={}, key={}, message={}",
                    bucket, key, e.getMessage(), e);

            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
        } catch (IOException e) {
            log.error("IOException during upload. bucket={}, key={}, message={}",
                    bucket, key, e.getMessage(), e);

            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    // AI 서버에 보낼 Presigned GET URL 생성
    public String generatePresignedGetUrl(String key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(presignedExpireSeconds))
                    .getObjectRequest(getObjectRequest)
                    .build();

            // AI 서버는 이 URL로 파일을 다운로드함
            return s3Presigner.presignGetObject(presignRequest)
                    .url()
                    .toString();
        } catch (S3Exception | SdkClientException e) {
            log.error("Failed to generate presigned URL. bucket={}, key={}, message={}",
                    bucket, key, e.getMessage(), e);

            throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    // 파일 검증 메서드
    private void validateVoiceFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(ErrorCode.EMPTY_FILE);
        }

        // Content-Type 정규화
        String normalizedContentType = normalizeContentType(file.getContentType());
        // 확장자 추출
        String extension = extractExtension(file.getOriginalFilename());

        boolean allowedContentType = normalizedContentType != null && ALLOWED_CONTENT_TYPES.contains(normalizedContentType);
        boolean allowedExtension = extension != null && ALLOWED_EXTENSIONS.contains(extension);

        if (!allowedContentType && !allowedExtension) {
            throw new CustomException(ErrorCode.INVALID_AUDIO_FILE);
        }
    }

    private String normalizeContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return null;
        }

        return contentType.split(";")[0].trim().toLowerCase(Locale.ROOT);
    }

    // 브라우저가 보내는 Content-Type을 기준으로 S3에 저장할 파일 확장자를 결정하는 메서드
    private String resolveExtension(MultipartFile file, String normalizedContentType) {
        // 1순위: contentType 기준으로 결정
        if (normalizedContentType != null) {
            return switch (normalizedContentType) {
                case "audio/webm", "video/webm" -> ".webm";
                case "audio/mp4", "audio/m4a", "audio/x-m4a" -> ".m4a";
                case "audio/mpeg", "audio/mp3" -> ".mp3";
                default -> resolveExtensionFromFilename(file.getOriginalFilename());
            };
        }

        // 2순위: 파일명 확장자
        return resolveExtensionFromFilename(file.getOriginalFilename());
    }

    private String resolveExtensionFromFilename(String originalFilename) {
        String extension = extractExtension(originalFilename);

        if (extension != null && ALLOWED_EXTENSIONS.contains(extension)) {
            return extension;
        }

        throw new CustomException(ErrorCode.INVALID_AUDIO_FILE);
    }

    // 확장자 추출 메서드
    private String extractExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return null;
        }

        return originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase(Locale.ROOT);
    }

    // S3 key 생성 메서드
    private String generateVoiceKey(Long memberId, String clientRequestId, String extension) {
        return voicePath + "/" + memberId + "/" + clientRequestId + extension;
    }
}
