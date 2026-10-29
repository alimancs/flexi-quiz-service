package com.flexi.quiz_service.repository;

import com.flexi.quiz_service.model.AttemptAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttemptAnswerRepository extends JpaRepository<AttemptAnswer, Long>{};