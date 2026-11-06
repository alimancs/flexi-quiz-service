package com.flexi.quiz_service.dto;

import java.util.List;

public record SaveAnswersRequestDto(
    List<PartialAnswerDto> answers
){};