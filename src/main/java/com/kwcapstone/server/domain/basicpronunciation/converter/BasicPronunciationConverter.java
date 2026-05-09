package com.kwcapstone.server.domain.basicpronunciation.converter;

import com.kwcapstone.server.domain.basicpronunciation.dto.response.BasicPronunciationPracticeResDTO;
import com.kwcapstone.server.domain.basicpronunciation.entity.BasicPronunciationPractice;
import com.kwcapstone.server.domain.basicpronunciation.enums.BasicVowel;
import com.kwcapstone.server.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BasicPronunciationConverter {
    public static BasicPronunciationPractice toPractice(
            Member member,
            String clientRequestId,
            BasicVowel targetVowel,
            String memberAudioKey,
            BigDecimal accuracyScore,
            String feedback
    ) {
        return BasicPronunciationPractice.builder()
                .member(member)
                .clientRequestId(clientRequestId)
                .targetVowel(targetVowel)
                .memberAudioKey(memberAudioKey)
                .accuracyScore(accuracyScore)
                .feedback(feedback)
                .build();
    }

    public static BasicPronunciationPracticeResDTO toPracticeResponse(
            BasicPronunciationPractice practice,
            String voiceUrl,
            String modelVoiceUrl
    ) {
        return new BasicPronunciationPracticeResDTO(
                practice.getId(),
                practice.getAccuracyScore(),
                practice.getFeedback(),
                voiceUrl,
                modelVoiceUrl
        );
    }
}
