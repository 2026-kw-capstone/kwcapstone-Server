package com.kwcapstone.server.domain.audio.service;

import com.kwcapstone.server.domain.audio.dto.request.CompleteAudioUploadReqDTO;
import com.kwcapstone.server.domain.audio.dto.request.PresignedUploadReqDTO;
import com.kwcapstone.server.domain.audio.dto.response.CompleteAudioUploadResDTO;
import com.kwcapstone.server.domain.audio.dto.response.LegacyUploadBenchmarkResDTO;
import com.kwcapstone.server.domain.audio.dto.response.PresignedUploadResDTO;
import org.springframework.web.multipart.MultipartFile;

public interface AudioUploadService {
    PresignedUploadResDTO generatePresignedUploadUrl(PresignedUploadReqDTO request);
    CompleteAudioUploadResDTO completeUpload(CompleteAudioUploadReqDTO request);
    LegacyUploadBenchmarkResDTO uploadLegacyForBenchmark(MultipartFile file);
}
