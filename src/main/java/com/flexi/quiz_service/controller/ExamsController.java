package com.flexi.quiz_service.controller;

import com.flexi.quiz_service.model.Exam;
import com.flexi.quiz_service.repository.ExamRepository;
import com.flexi.quiz_service.types.ExamStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exams")
public class ExamsController {

    private final ExamRepository examRepository;

    public ExamsController(ExamRepository examRepository) {
        this.examRepository=examRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Exam> getExam(@PathVariable Long id) {
        Exam e = examRepository.findById(id).orElseThrow(()-> new RuntimeException("Exam with id: "+id+" not found"));
        return ResponseEntity.ok(e);
    }
    
    // Per-topic accuracy across all attempts (lecturer) 
    @GetMapping("/{id}/analytics/topics")
    public String getExamAnalytics(@PathVariable String id) {
        return "Success getting exam analytics of exam: "+id;
    }

    // Ranked results 
    @GetMapping("/{id}/leaderboard")
    public String getExamLeaderboard(@PathVariable String id) {
        return "Success getting exam leaderboard: "+id;
    }

    @GetMapping
    public String getExams() {
        return "Success getting exams list";
    }

    @PostMapping
    public String createExam() {
        return "Success creating exam";
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<String> publishExam(@PathVariable Long id) {
        Exam e = examRepository.findById(id).orElseThrow(()->new RuntimeException("Exam with id: "+id+" not found"));
        e.setExamStatus(ExamStatus.PUBLISHED);
        examRepository.save(e);
        return ResponseEntity.ok("Success locking and publishing exam: "+id);
    }
    
    // Start attempt. Returns `attemptId`, `token`, `expiresAt` and the questions **without** `is_correct`
    @PostMapping("/{examId}/attempts")
    public String startExamAttempt(@PathVariable String examId) {
        return "Success starting exam attempt";
    }

    @PutMapping("/{id}/questions")
    public String setQuestionList(@PathVariable String id) {
        return "Success setting question list and marks";
    }

    @DeleteMapping("{id}") 
    public String deleteExam(@PathVariable String id) {
        return "Success deleting exam";
    }
}