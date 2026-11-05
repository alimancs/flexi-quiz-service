package com.flexi.quiz_service.controller;

import com.flexi.quiz_service.model.ExamAttempt;
import com.flexi.quiz_service.model.Student;
import com.flexi.quiz_service.repository.StudentRepository;
import com.flexi.quiz_service.service.ExamAttemptService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
public class StudentsController {
    private final StudentRepository studentRepository;

    private final ExamAttemptService examAttemptService;

    public StudentsController(StudentRepository studentRepository, ExamAttemptService examAttemptService) {
        this.studentRepository=studentRepository;
        this.examAttemptService=examAttemptService;
    }

    @GetMapping("/{id}")
    public ResponseEntity getStudent(@PathVariable Long id) {
        Student s = studentRepository.findById(id).orElse(null);
        Map<String, Object> resBody = Map.of(
            "student", s
        );
        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }

    @GetMapping("/{id}/attempts")
    public ResponseEntity getStudentAttempts(@PathVariable Long id) {
        List<ExamAttempt> attempts = examAttemptService.getAttemptsForStudent(id);
        Map<String, Object> resBody = Map.of(
            "attempts", attempts
        );
        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }

    @PostMapping
    public ResponseEntity createStudent(@RequestBody Student req) {
        Student newStudent = new Student(req.getFullname(), req.getPassword(), req.getEmail());
        Student s = studentRepository.save(newStudent);
        Map<String, Object> resBody = Map.of(
            "student", s
        );
        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }

    
}