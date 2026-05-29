package com.kwcapstone.server.domain.home.service;

import com.kwcapstone.server.domain.basicpronunciation.repository.BasicPronunciationPracticeRepository;
import com.kwcapstone.server.domain.conversation.repository.ConversationRepository;
import com.kwcapstone.server.domain.home.converter.HomeConverter;
import com.kwcapstone.server.domain.home.dto.response.ContinueLearningResDTO;
import com.kwcapstone.server.domain.home.dto.response.WeeklySummaryResDTO;
import com.kwcapstone.server.domain.member.exception.code.MemberErrorCode;
import com.kwcapstone.server.domain.member.repository.MemberRepository;
import com.kwcapstone.server.domain.mysentence.repository.MySentenceRepository;
import com.kwcapstone.server.domain.mysentence.repository.MySentenceAnalysisResultRepository;
import com.kwcapstone.server.domain.scenario.repository.ScenarioAnalysisResultRepository;
import com.kwcapstone.server.global.apiPayload.exception.CustomException;
import com.kwcapstone.server.global.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeServiceImpl implements HomeService {
    private final MemberRepository memberRepository;
    private final BasicPronunciationPracticeRepository basicPronunciationPracticeRepository;
    private final MySentenceRepository mySentenceRepository;
    private final MySentenceAnalysisResultRepository mySentenceAnalysisResultRepository;
    private final ScenarioAnalysisResultRepository scenarioAnalysisResultRepository;
    private final ConversationRepository conversationRepository;

    @Override
    public WeeklySummaryResDTO getWeeklySummary() {
        Long memberId = SecurityUtil.getCurrentMemberId();

        if (!memberRepository.existsById(memberId)) {
            throw new CustomException(MemberErrorCode.MEMBER_NOT_FOUND);
        }

        LocalDateTime thisWeekStartAt = LocalDate.now(ZoneId.of("Asia/Seoul"))
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .atStartOfDay();
        LocalDateTime nextWeekStartAt = thisWeekStartAt.plusWeeks(1);
        LocalDateTime lastWeekStartAt = thisWeekStartAt.minusWeeks(1);

        TrainingStats thisWeekStats = getTrainingStats(memberId, thisWeekStartAt, nextWeekStartAt);
        TrainingStats lastWeekStats = getTrainingStats(memberId, lastWeekStartAt, thisWeekStartAt);

        return HomeConverter.toWeeklySummaryResDTO(
                thisWeekStats.totalCount(),
                thisWeekStats.totalCount() > lastWeekStats.totalCount(),
                calculateAverage(thisWeekStats.pronunciationScoreSum(), thisWeekStats.pronunciationCount()),
                calculateAverage(thisWeekStats.meaningDeliveryScoreSum(), thisWeekStats.scenarioCount())
        );
    }

    @Override
    public ContinueLearningResDTO getContinueLearning() {
        Long memberId = SecurityUtil.getCurrentMemberId();

        if (!memberRepository.existsById(memberId)) {
            throw new CustomException(MemberErrorCode.MEMBER_NOT_FOUND);
        }

        List<ContinueLearningResDTO.Content> contents = new ArrayList<>();

        scenarioAnalysisResultRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId)
                .map(HomeConverter::toScenarioContent)
                .ifPresent(contents::add);

        mySentenceRepository.findTopByMemberIdAndDeletedAtIsNullOrderByCreatedAtDesc(memberId)
                .map(latestSentence -> HomeConverter.toMySentenceContent(
                        latestSentence,
                        mySentenceRepository.countByMemberIdAndDeletedAtIsNull(memberId)
                ))
                .ifPresent(contents::add);

        basicPronunciationPracticeRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId)
                .map(HomeConverter::toBasicPracticeContent)
                .ifPresent(contents::add);

        conversationRepository.findTopByMemberIdOrderByLastMessageAtDesc(memberId)
                .map(HomeConverter::toFreeTalkContent)
                .ifPresent(contents::add);

        contents.sort(Comparator.comparing(ContinueLearningResDTO.Content::getLatestCreatedAt).reversed());

        return HomeConverter.toContinueLearningResDTO(contents);
    }

    private TrainingStats getTrainingStats(
            Long memberId,
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {
        long basicCount = basicPronunciationPracticeRepository
                .countByMemberIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                        memberId,
                        startAt,
                        endAt
                );
        long mySentenceCount = mySentenceAnalysisResultRepository
                .countByMemberIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                        memberId,
                        startAt,
                        endAt
                );
        long scenarioCount = scenarioAnalysisResultRepository
                .countByMemberIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                        memberId,
                        startAt,
                        endAt
                );

        BigDecimal pronunciationScoreSum = nullToZero(
                basicPronunciationPracticeRepository.sumAccuracyScoreByMemberIdAndPeriod(
                        memberId,
                        startAt,
                        endAt
                )
        ).add(nullToZero(
                mySentenceAnalysisResultRepository.sumPronunciationScoreByMemberIdAndPeriod(
                        memberId,
                        startAt,
                        endAt
                )
        )).add(nullToZero(
                scenarioAnalysisResultRepository.sumPronunciationScoreByMemberIdAndPeriod(
                        memberId,
                        startAt,
                        endAt
                )
        ));

        BigDecimal meaningDeliveryScoreSum = nullToZero(
                scenarioAnalysisResultRepository.sumMeaningDeliveryScoreByMemberIdAndPeriod(
                        memberId,
                        startAt,
                        endAt
                )
        );

        return new TrainingStats(
                basicCount,
                mySentenceCount,
                scenarioCount,
                pronunciationScoreSum,
                meaningDeliveryScoreSum
        );
    }

    private int calculateAverage(BigDecimal scoreSum, long count) {
        if (count == 0) {
            return 0;
        }

        return scoreSum.divide(BigDecimal.valueOf(count), 0, RoundingMode.HALF_UP)
                .intValue();
    }

    private BigDecimal nullToZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private record TrainingStats(
            long basicCount,
            long mySentenceCount,
            long scenarioCount,
            BigDecimal pronunciationScoreSum,
            BigDecimal meaningDeliveryScoreSum
    ) {
        long totalCount() {
            return basicCount + mySentenceCount + scenarioCount;
        }

        long pronunciationCount() {
            return totalCount();
        }
    }
}
