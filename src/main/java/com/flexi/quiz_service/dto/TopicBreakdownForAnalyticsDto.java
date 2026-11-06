package com.flexi.quiz_service.dto;

import java.util.List;

public record TopicBreakdownForAnalyticsDto(
    String name,
    String description,
    List<AnswerForResultDto> answers,
    Integer wrong_count,
    Integer correct_count,
    Integer totalMark
){};