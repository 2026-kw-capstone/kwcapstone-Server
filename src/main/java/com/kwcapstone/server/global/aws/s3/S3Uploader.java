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

    // S3 업로드 후 public URL이 아니라 object key를 반환
    public String uploadVoiceFile(Long memberId, String clientRequestId, MultipartFile file) {
        // 파일 검증
        validateVoiceFile(file);

        // 확장자 추출
        String extension = extractExtension(file); // ex) abc.webm -> .webm
        // S3 key(=S3 파일 경로) 생성
        String key = generateVoiceKey(memberId, clientRequestId, extension);

        log.info("S3 upload start. bucket={}, key={}, originalFilename={}, contentType={}, size={}",
                bucket, key, file.getOriginalFilename(), file.getContentType(), file.getSize());

        try {
            // 업로드 요청 객체 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            // S3 업로드
            s3Client.putObject(putObjectRequest,
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

        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();

        boolean webmContentType = contentType != null && contentType.equalsIgnoreCase("audio/webm");
        boolean webmFileName = originalFilename != null && originalFilename.toLowerCase().endsWith(".webm");

        if (!webmContentType && !webmFileName) {
            throw new CustomException(ErrorCode.INVALID_AUDIO_FILE);
        }
    }

    // 확장자 추출 메서드
    private String extractExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        return ".webm";
    }

    // S3 key 생성 메서드
    private String generateVoiceKey(Long memberId, String clientRequestId, String extension) {
        return voicePath + "/" + memberId + "/" + clientRequestId + extension;
    }
}
