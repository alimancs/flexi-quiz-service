package com.flexi.quiz_service.repository;

import com.flexi.quiz_service.model.MainQuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionOptionRepository extends JpaRepository<MainQuestionOption, Long>{};