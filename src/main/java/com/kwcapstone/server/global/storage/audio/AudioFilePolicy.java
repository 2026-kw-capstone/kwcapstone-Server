package com.kwcapstone.server.global.storage.audio;

import com.kwcapstone.server.global.apiPayload.exception.CustomException;
import com.kwcapstone.server.global.apiPayload.response.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;
import java.util.Set;

@Component
public class AudioFilePolicy {
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

    // 파일 검증 메서드
    public void validate(MultipartFile file) {
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

    public String normalizeContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return null;
        }

        return contentType.split(";")[0].trim().toLowerCase(Locale.ROOT);
    }

    // 브라우저가 보내는 Content-Type을 기준으로 S3에 저장할 파일 확장자를 결정하는 메서드
    public String resolveExtension(MultipartFile file, String normalizedContentType) {
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
}
