package com.flexi.quiz_service.dto;

import java.util.List;

public record AnswerForResultDto(
    String question,
    Integer mark,
    QuestionOptionForResultDto selection,
    List<QuestionOptionForResultDto> options
){};