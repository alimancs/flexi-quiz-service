package com.flexi.quiz_service.dto;

public record AttemptAnalyticsSummaryDto(
    Integer total,
    Integer completed,
    Integer inProgress,
    Integer expired
) {}
