package com.flexi.quiz_service.dto;

import java.util.List;

public record ExamAnalyticsDto(
    Long examId,
    String title,
    AttemptAnalyticsSummaryDto attempts,
    ScoreAnalyticsDto scores,
    List<TopicAnalyticsDto> topics
) {}
