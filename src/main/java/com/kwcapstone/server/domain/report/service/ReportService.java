package com.kwcapstone.server.domain.report.service;

import com.kwcapstone.server.domain.report.dto.response.PronunciationAccuracyReportResDTO;

public interface ReportService {
    PronunciationAccuracyReportResDTO getPronunciationAccuracy(String period, String type, String baseDate);
}
