package com.flexi.quiz_service.dto;


public record LeaderBoardPositionDto(
    StudentDto student,
    Integer score,
    Integer position
){};