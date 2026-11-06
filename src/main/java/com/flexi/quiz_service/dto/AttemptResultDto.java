package com.flexi.quiz_service.dto;

import com.flexi.quiz_service.model.Exam;
import com.flexi.quiz_service.types.AttemptStatus;
import java.time.Instant;
import java.util.List;

public record AttemptResultDto(
    Exam exam,
    StudentDto student,
    List<AnswerForResultDto> answers,
    AttemptStatus status,
    Instant started_at,
    Instant expires_at,
    Instant submitted_at,
    Instant updated_at,
    Integer score,
    Integer correct_count,
    Integer wrong_count,
    Integer unanswered_count
){};