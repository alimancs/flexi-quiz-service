package com.flexi.quiz_service.controller;

import com.flexi.quiz_service.dto.QuestionForExamDto;
import com.flexi.quiz_service.dto.SetQuestionRequestDto;
import com.flexi.quiz_service.model.Exam;
import com.flexi.quiz_service.model.ExamAttempt;
import com.flexi.quiz_service.model.Student;
import com.flexi.quiz_service.repository.ExamAttemptRepository;
import com.flexi.quiz_service.repository.ExamQuestionRepository;
import com.flexi.quiz_service.repository.ExamRepository;
import com.flexi.quiz_service.repository.QuestionOptionRepository;
import com.flexi.quiz_service.repository.QuestionRepository;
import com.flexi.quiz_service.repository.StudentRepository;
import com.flexi.quiz_service.service.ExamQuestionService;
import com.flexi.quiz_service.types.ExamStatus;
import com.flexi.quiz_service.util.Generating;
import com.flexi.quiz_service.util.Hashing;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;


@RestController
@RequestMapping("/exams")
public class ExamsController {

    private final ExamRepository examRepository;
    private final ExamAttemptRepository attemptRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final QuestionRepository questionRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final StudentRepository studentRepository;

    private final ExamQuestionService examQuestionService;

    public ExamsController(
        ExamRepository examRepository, 
        ExamAttemptRepository attemptRepository, 
        ExamQuestionService examQuestionService,
        QuestionOptionRepository questionOptionRepository,
        QuestionRepository questionRepository,
        ExamQuestionRepository examQuestionRepository,
        StudentRepository studentRepository
        ) {
        this.examRepository=examRepository;
        this.attemptRepository=attemptRepository;
        this.examQuestionService=examQuestionService;
        this.questionOptionRepository=questionOptionRepository;
        this.questionRepository=questionRepository;
        this.examQuestionRepository=examQuestionRepository;
        this.studentRepository=studentRepository;
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
    public ResponseEntity getExams() {
        List<Exam> exams = examRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(exams);
    }

    @PostMapping
    public ResponseEntity createExam(@RequestBody Exam newExam) {
        if (newExam==null) {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request");
        };

        Exam e = examRepository.save(newExam);
        return ResponseEntity.status(HttpStatus.CREATED).body(e);
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
    public ResponseEntity<?> startExamAttempt(@PathVariable Long examId, @RequestBody JsonNode body) {
        JsonNode studentIdNode = body.get("studentId");
        if (studentIdNode==null || studentIdNode.isEmpty()) {
            return ResponseEntity.status(400).body("Invalid student id");
        }
        Long studentId = studentIdNode.asLong();
        String token = Generating.uniqueToken();
        String tokenHash = Hashing.sha256Hex(token);

        // get all exam questions and options
        List<QuestionForExamDto> questions = examQuestionService.getQuestionsForExam(examId);

        // Expires 2 hours from now
        Instant expiresAt = Instant.now().plus(2, ChronoUnit.HOURS);

        Exam e = examRepository.findById(examId).orElseThrow(()->new RuntimeException("Exam with id: "+examId+" not found"));
        Student s = studentRepository.findById(studentId).orElseThrow(()->new RuntimeException("Student with id: "+studentId+" not found"));

        ExamAttempt a = new ExamAttempt(e, s, tokenHash, expiresAt);

        Map<String, Object> responseBody = Map.of(
            "attempId", a.getId(),
            "token", token,
            "expiresAt", expiresAt,
            "questions", questions
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @PutMapping("/{examId}/questions")
    public ResponseEntity setQuestionList(@PathVariable Long examId, @RequestBody SetQuestionRequestDto body) {
        QuestionForExamDto eq = examQuestionService.addQuestionToExam(examId, body.question(), body.options());

        return ResponseEntity.status(HttpStatus.CREATED).body(eq);
    }

    @DeleteMapping("{id}") 
    public ResponseEntity deleteExam(@PathVariable Long id) {
        examRepository.deleteById(id);
        return ResponseEntity.ok("Exam removed successfully");
    }

}