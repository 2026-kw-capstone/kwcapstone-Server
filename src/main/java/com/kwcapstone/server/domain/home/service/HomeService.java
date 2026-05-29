package com.kwcapstone.server.domain.home.service;

import com.kwcapstone.server.domain.home.dto.response.ContinueLearningResDTO;
import com.kwcapstone.server.domain.home.dto.response.WeeklySummaryResDTO;

public interface HomeService {
    WeeklySummaryResDTO getWeeklySummary();
    ContinueLearningResDTO getContinueLearning();
}
