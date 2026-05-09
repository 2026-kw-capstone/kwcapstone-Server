package com.kwcapstone.server.domain.basicpronunciation.dto.request;

import com.kwcapstone.server.domain.basicpronunciation.enums.BasicVowel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class BasicPronunciationPracticeReqDTO {
    @NotBlank
    private String clientRequestId;

    @NotNull
    private BasicVowel targetVowel;

    @NotNull
    private MultipartFile voiceFile;
}
