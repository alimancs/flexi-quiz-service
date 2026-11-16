package com.flexi.quiz_service.dto;


public record TopicAnalyticsDto(
    Long topicId,
    String name,
    Integer questions,
    Integer answered,
    Integer correct,
    Integer incorrect,
    Integer unanswered,
    Double accuracyPercent,
    Double averageMarksPercent
) {}