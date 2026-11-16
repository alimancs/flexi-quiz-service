package com.flexi.quiz_service.service;

import com.flexi.quiz_service.model.ExamAttempt;
import com.flexi.quiz_service.repository.ExamAttemptRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ExamAttemptService {

    private final ExamAttemptRepository examAttemptRepository;

    public ExamAttemptService(ExamAttemptRepository examAttemptRepository) {
        this.examAttemptRepository=examAttemptRepository;
    };

    @Transactional(readOnly=true)
    public List<ExamAttempt> getAttemptsForStudent(Long studentId) {
        List<ExamAttempt> attempts = examAttemptRepository.findForStudent(studentId);
        if (attempts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No attempts or invalid student id");
        } 
        return attempts;
    }
}