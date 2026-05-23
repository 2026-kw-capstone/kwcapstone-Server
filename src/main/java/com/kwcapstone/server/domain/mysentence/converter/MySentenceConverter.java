package com.kwcapstone.server.domain.mysentence.converter;

import com.kwcapstone.server.domain.member.entity.Member;
import com.kwcapstone.server.domain.mysentence.client.dto.response.PronunciationAnalyzeAiResDTO;
import com.kwcapstone.server.domain.mysentence.dto.request.MySentenceCreateReqDTO;
import com.kwcapstone.server.domain.mysentence.dto.response.*;
import com.kwcapstone.server.domain.mysentence.entity.MySentence;
import com.kwcapstone.server.domain.mysentence.entity.MySentenceAnalysisResult;

import java.util.List;

public class MySentenceConverter {

    private MySentenceConverter() {
    }

    public static MySentence toMySentence(MySentenceCreateReqDTO request, Member member) {
        return MySentence.builder()
                .member(member)
                .sentenceContent(request.getSentenceContent().trim())
                .build();
    }

    public static MySentenceCreateResDTO toCreateResponse(MySentence mySentence) {
        return new MySentenceCreateResDTO(
                mySentence.getId(),
                mySentence.getSentenceContent()
        );
    }

    public static MySentenceListResDTO.MySentenceInfo toMySentenceInfo(MySentence mySentence) {
        return new MySentenceListResDTO.MySentenceInfo(
                mySentence.getId(),
                mySentence.getSentenceContent()
        );
    }

    public static MySentenceListResDTO toListResponse(List<MySentence> mySentences) {
        return new MySentenceListResDTO(
                mySentences.stream()
                        .map(MySentenceConverter::toMySentenceInfo)
                        .toList()
        );
    }

    public static MySentenceDetailResDTO toDetailResponse(MySentence mySentence) {
        return new MySentenceDetailResDTO(
                mySentence.getId(),
                mySentence.getSentenceContent()
        );
    }

    public static List<MySentenceAnalyzeResDTO.WordAnalysis> toWordAnalysisResponse(
            PronunciationAnalyzeAiResDTO aiResponse
    ) {
        return aiResponse.getWordAnalysis().stream()
                .map(word -> new MySentenceAnalyzeResDTO.WordAnalysis(
                        word.getRefChar(),
                        word.getHypChar(),
                        word.getGrade()
                ))
                .toList();
    }

    public static MySentenceAnalyzeResDTO toAnalyzeResponse(
            MySentenceAnalysisResult result,
            List<MySentenceAnalyzeResDTO.WordAnalysis> wordAnalysis
    ) {
        return new MySentenceAnalyzeResDTO(
                result.getId(),
                result.getMySentence().getId(),
                result.getPronunciationScore(),
                result.getSpeechRateScore(),
                result.getSilenceRatio(),
                result.getAiFeedback(),
                wordAnalysis
        );
    }
}
