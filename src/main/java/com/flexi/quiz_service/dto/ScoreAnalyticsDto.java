package com.flexi.quiz_service.dto;

public record ScoreAnalyticsDto(
    Double average,
    Double median,
    Integer highest,
    Integer lowest,
    Double averagePercent
) {}
