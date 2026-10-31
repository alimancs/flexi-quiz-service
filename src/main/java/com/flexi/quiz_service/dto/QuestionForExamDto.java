package com.flexi.quiz_service.dto;

import com.flexi.quiz_service.types.QuestionDifficulty;
import java.util.List;

public record QuestionForExamDto(
    Long examQuestionId,
    Long questionId,
    String text,
    QuestionDifficulty difficulty,
    Integer position,
    Integer marks,
    List<OptionForExamDto> options
){};