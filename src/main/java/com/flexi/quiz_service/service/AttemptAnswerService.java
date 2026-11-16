package com.flexi.quiz_service.service;

import com.flexi.quiz_service.model.AttemptAnswer;
import com.flexi.quiz_service.repository.AttemptAnswerRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AttemptAnswerService{
    private final AttemptAnswerRepository attemptAnswerRepository;

    public AttemptAnswerService(
        AttemptAnswerRepository attemptAnswerRepository
    ) {
        this.attemptAnswerRepository=attemptAnswerRepository;
    };

    public List<AttemptAnswer> getAnswersForAttempt(Long attemptId) {
        List<AttemptAnswer> answers = attemptAnswerRepository.findForAttempt(attemptId);
        return answers;
    }
}