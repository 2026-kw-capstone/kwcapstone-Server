package com.kwcapstone.server.global.storage.audio;

import com.kwcapstone.server.global.apiPayload.exception.CustomException;
import com.kwcapstone.server.global.apiPayload.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3AudioStorageService implements AudioStorageService {
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final AudioFilePolicy audioFilePolicy;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.audio-root-path}")
    private String audioRootPath;

    @Value("${cloud.aws.s3.presigned-expire-seconds}")
    private long presignedExpireSeconds;

    @Override // S3 업로드 후 public URL이 아닌 object key를 반환
    public String upload(String keyPrefix, String fileBaseName, MultipartFile file) {
        // 파일 검증
        audioFilePolicy.validate(file);

        // Content-Type 정규화, ex) audio/webm;codecs=opus -> audio/webm
        String normalizedContentType = audioFilePolicy.normalizeContentType(file.getContentType());
        // 확장자 추출
        String extension = audioFilePolicy.resolveExtension(file, normalizedContentType); // ex) abc.webm -> .webm
        // S3 key(=S3 파일 경로) 생성
        String key = buildKey(keyPrefix, fileBaseName, extension);

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

    @Override // S3 객체 삭제 메서드
    public void delete(String key) {
        if (key == null || key.isBlank()) {
            return;
        }

        log.info("S3 delete start. bucket={}, key={}", bucket, key);

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);

            log.info("S3 delete success. bucket={}, key={}", bucket, key);
        } catch (S3Exception e) {
            log.error("S3Exception during delete. bucket={}, key={}, statusCode={}, errorCode={}, errorMessage={}",
                    bucket,
                    key,
                    e.statusCode(),
                    e.awsErrorDetails() != null ? e.awsErrorDetails().errorCode() : null,
                    e.awsErrorDetails() != null ? e.awsErrorDetails().errorMessage() : e.getMessage(),
                    e
            );

            throw new CustomException(ErrorCode.FILE_DELETE_FAILED);
        } catch (SdkClientException e) {
            log.error("SdkClientException during delete. bucket={}, key={}, message={}",
                    bucket, key, e.getMessage(), e);

            throw new CustomException(ErrorCode.FILE_DELETE_FAILED);
        }
    }

    @Override
    public void deleteAll(Collection<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return;
        }

        keys.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(key -> !key.isBlank())
                .distinct()
                .forEach(this::delete);
    }

    @Override // AI 서버에 보낼 Presigned GET URL 생성
    public String generatePresignedGetUrl(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }

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

    // S3 key(=S3 파일 경로) 생성 메서드
    private String buildKey(String keyPrefix, String fileBaseName, String extension) {
        String normalizedRoot = trimSlashes(audioRootPath);
        String normalizedPrefix = trimSlashes(keyPrefix);
        String normalizedBaseName = sanitizeFileBaseName(fileBaseName);

        if (normalizedPrefix.isBlank()) {
            return normalizedRoot + "/" + normalizedBaseName + extension;
        }

        return normalizedRoot + "/" + normalizedPrefix + "/" + normalizedBaseName + extension;
    }

    // 경로 문자열의 앞뒤 / 제거 메서드(경로 정규화)
    private String trimSlashes(String value) {
        if (value == null) {
            return "";
        }

        return value.replaceAll("^/+", "").replaceAll("/+$", "").trim();
    }

    // Path Traversal 공격 방지
    private String sanitizeFileBaseName(String value) {
        if (value == null || value.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        return value.trim()
                .replace("/", "_")
                .replace("\\", "_");
    }
}
