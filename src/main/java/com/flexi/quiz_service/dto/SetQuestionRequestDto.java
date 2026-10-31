package com.flexi.quiz_service.dto;

import com.flexi.quiz_service.model.MainQuestion;
import com.flexi.quiz_service.model.MainQuestionOption;
import java.util.List;

public record SetQuestionRequestDto(
    MainQuestion question,
    List<MainQuestionOption> options
){};