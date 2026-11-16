package com.flexi.quiz_service.controller;

import com.flexi.quiz_service.dto.AttemptAnalyticsSummaryDto;
import com.flexi.quiz_service.dto.ExamAnalyticsDto;
import com.flexi.quiz_service.dto.LeaderBoardPositionDto;
import com.flexi.quiz_service.dto.QuestionForExamDto;
import com.flexi.quiz_service.dto.ScoreAnalyticsDto;
import com.flexi.quiz_service.dto.SetQuestionRequestDto;
import com.flexi.quiz_service.dto.StudentDto;
import com.flexi.quiz_service.dto.TopicAnalyticsDto;
import com.flexi.quiz_service.model.AttemptAnswer;
import com.flexi.quiz_service.model.Exam;
import com.flexi.quiz_service.model.ExamAttempt;
import com.flexi.quiz_service.model.ExamQuestion;
import com.flexi.quiz_service.model.Student;
import com.flexi.quiz_service.model.Topic;
import com.flexi.quiz_service.repository.AttemptAnswerRepository;
import com.flexi.quiz_service.repository.ExamAttemptRepository;
import com.flexi.quiz_service.repository.ExamQuestionRepository;
import com.flexi.quiz_service.repository.ExamRepository;
import com.flexi.quiz_service.repository.QuestionOptionRepository;
import com.flexi.quiz_service.repository.QuestionRepository;
import com.flexi.quiz_service.repository.StudentRepository;
import com.flexi.quiz_service.service.ExamQuestionService;
import com.flexi.quiz_service.types.AttemptStatus;
import com.flexi.quiz_service.types.ExamStatus;
import com.flexi.quiz_service.util.Generating;
import com.flexi.quiz_service.util.Hashing;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;


@RestController
@RequestMapping("/exams")
public class ExamsController {

    private final ExamRepository examRepository;
    private final ExamAttemptRepository attemptRepository;
    private final AttemptAnswerRepository attemptAnswerRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final QuestionRepository questionRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final StudentRepository studentRepository;

    private final ExamQuestionService examQuestionService;

    public ExamsController(
        ExamRepository examRepository, 
        ExamAttemptRepository attemptRepository, 
        AttemptAnswerRepository attemptAnswerRepository,
        ExamQuestionService examQuestionService,
        QuestionOptionRepository questionOptionRepository,
        QuestionRepository questionRepository,
        ExamQuestionRepository examQuestionRepository,
        StudentRepository studentRepository
        ) {
        this.examRepository=examRepository;
        this.attemptRepository=attemptRepository;
        this.attemptAnswerRepository=attemptAnswerRepository;
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
    
    // Per-topic performance aggregated from submitted attempts.
    @GetMapping("/{id}/analytics/topics")
    public ResponseEntity<?> getExamAnalytics(@PathVariable Long id) {
        Exam e = examRepository.findById(id).orElse(null);
        if (e==null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found/ or id is invalid");
        };
        List<ExamAttempt> examAttempts = attemptRepository.findForExamDescendingByScore(e.getId());
        int completedAttempts = 0;
        int inProgressAttempts = 0;
        int expiredAttempts = 0;
        List<Integer> completedScores = new ArrayList<>();

        for (ExamAttempt attempt : examAttempts) {
            if (attempt.getStatus() == AttemptStatus.SUBMITTED) {
                completedAttempts++;
                completedScores.add(Objects.requireNonNullElse(attempt.getScore(), 0));
            } else if (attempt.getStatus() == AttemptStatus.IN_PROGRESS) {
                inProgressAttempts++;
            } else if (attempt.getStatus() == AttemptStatus.EXPIRED) {
                expiredAttempts++;
            }
        }

        completedScores.sort(Comparator.naturalOrder());
        Double averageScore = completedScores.isEmpty()
            ? null
            : completedScores.stream().mapToInt(Integer::intValue).average().orElseThrow();
        Double medianScore = calculateMedian(completedScores);
        Integer highestScore = completedScores.isEmpty() ? null : completedScores.get(completedScores.size() - 1);
        Integer lowestScore = completedScores.isEmpty() ? null : completedScores.get(0);

        List<ExamQuestion> examQuestions = examQuestionRepository.findForExam(id);
        Map<Long, TopicAccumulator> topicStats = new LinkedHashMap<>();
        int submittedAttemptCount = completedAttempts;

        for (ExamQuestion eq:examQuestions) {
            Topic currentTopic = eq.getQuestion().getTopic();
            topicStats.computeIfAbsent(currentTopic.getId(), topicId ->
                new TopicAccumulator(topicId, currentTopic.getName())
            ).questions++;
        };

        List<AttemptAnswer> answers = attemptAnswerRepository.findForExamAndAttemptStatus(id, AttemptStatus.SUBMITTED);
        for (AttemptAnswer answer : answers) {
            Long topicId = answer.getQuestion().getTopic().getId();
            TopicAccumulator stats = topicStats.get(topicId);
            if (stats != null) {
                stats.answered++;
                if (Boolean.TRUE.equals(answer.getIsCorrect())) {
                    stats.correct++;
                }
            }
        }

        List<TopicAnalyticsDto> topics = topicStats.values().stream()
            .map(stats -> {
                int unanswered = Math.max(0, stats.questions * submittedAttemptCount - stats.answered);
                Double accuracyPercent = stats.answered == 0
                    ? null
                    : percentage(stats.correct, stats.answered);
                int possibleMarks = stats.questions * submittedAttemptCount * Objects.requireNonNullElse(e.getScorePerQuestion(), 0);
                Double averageMarksPercent = possibleMarks == 0
                    ? null
                    : percentage(stats.correct * Objects.requireNonNullElse(e.getScorePerQuestion(), 0), possibleMarks);
                return new TopicAnalyticsDto(
                    stats.topicId,
                    stats.name,
                    stats.questions,
                    stats.answered,
                    stats.correct,
                    stats.answered - stats.correct,
                    unanswered,
                    accuracyPercent,
                    averageMarksPercent
                );
            })
            .toList();

        int maximumScore = examQuestions.size() * Objects.requireNonNullElse(e.getScorePerQuestion(), 0);
        Double averagePercent = completedScores.isEmpty() || maximumScore == 0
            ? null
            : percentage(averageScore, maximumScore);

        ExamAnalyticsDto analytics = new ExamAnalyticsDto(
            e.getId(),
            e.getTitle(),
            new AttemptAnalyticsSummaryDto(examAttempts.size(), completedAttempts, inProgressAttempts, expiredAttempts),
            new ScoreAnalyticsDto(averageScore, medianScore, highestScore, lowestScore, averagePercent),
            topics
        );
        return ResponseEntity.ok(analytics);
    }

    private static Double calculateMedian(List<Integer> sortedScores) {
        int count = sortedScores.size();
        if (count == 0) {
            return null;
        }
        if (count % 2 == 1) {
            return (double) sortedScores.get(count / 2);
        }
        return (sortedScores.get(count / 2 - 1) + sortedScores.get(count / 2)) / 2.0;
    }

    private static Double percentage(double value, double maximum) {
        return Math.round(value / maximum * 1000.0) / 10.0;
    }

    private static final class TopicAccumulator {
        private final Long topicId;
        private final String name;
        private int questions;
        private int answered;
        private int correct;

        private TopicAccumulator(Long topicId, String name) {
            this.topicId = topicId;
            this.name = name;
        }
    }

    // Ranked results 
    @GetMapping("/{id}/leaderboard")
    public ResponseEntity getExamLeaderboard(@PathVariable Long id) {
        Exam e = examRepository.findById(id).orElse(null);
        if (e==null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found/ or id is invalid");
        }

        List<ExamAttempt> examAttempts = attemptRepository.findForExamDescendingByScore(e.getId());
        List<LeaderBoardPositionDto> positions = new ArrayList<>();
        
        Integer counter = 1;
        for (ExamAttempt ea :examAttempts) {
            Student s = ea.getStudent();
            positions.add( new LeaderBoardPositionDto(new StudentDto(s.getFullname(), s.getEmail()), ea.getScore(), counter));
            counter++;
        };

        Map<String, Object> res = Map.of(
            "positions",positions
        );

        return ResponseEntity.ok(res);
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