package com.kwcapstone.server.global.storage.audio;

import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

public interface AudioStorageService {
    String upload(String keyPrefix, String fileBaseName, MultipartFile file);
    void delete(String key);
    void deleteAll(Collection<String> keys);
    String generatePresignedGetUrl(String key);
}
