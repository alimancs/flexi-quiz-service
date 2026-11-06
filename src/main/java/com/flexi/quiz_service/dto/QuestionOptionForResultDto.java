package com.flexi.quiz_service.dto;

import com.flexi.quiz_service.types.OptionLabel;

public record QuestionOptionForResultDto(
    String text,
    Boolean isCorrect,
    OptionLabel label
){};