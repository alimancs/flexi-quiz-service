package com.flexi.quiz_service.dto;

public record PartialAnswerDto(
    Long selectedOptionId,
    Long questionId
){}