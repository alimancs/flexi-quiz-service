package com.flexi.quiz_service.controller;

import com.flexi.quiz_service.dto.AnswerForResultDto;
import com.flexi.quiz_service.dto.AttemptAnalyticsDto;
import com.flexi.quiz_service.dto.AttemptResultDto;
import com.flexi.quiz_service.dto.PartialAnswerDto;
import com.flexi.quiz_service.dto.QuestionOptionForResultDto;
import com.flexi.quiz_service.dto.SaveAnswersRequestDto;
import com.flexi.quiz_service.dto.StudentDto;
import com.flexi.quiz_service.dto.TopicBreakdownForAnalyticsDto;
import com.flexi.quiz_service.model.AttemptAnswer;
import com.flexi.quiz_service.model.ExamAttempt;
import com.flexi.quiz_service.model.Topic;
import com.flexi.quiz_service.repository.AttemptAnswerRepository;
import com.flexi.quiz_service.repository.ExamAttemptRepository;
import com.flexi.quiz_service.service.AttemptAnswerService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/attempts")
public class AttemptsController {

    private final ExamAttemptRepository examAttemptRepository;
    private final AttemptAnswerRepository attemptAnswerRepository;

    private final AttemptAnswerService attemptAnswerService;

    public AttemptsController(
        ExamAttemptRepository examAttemptRepository,
        AttemptAnswerRepository attemptAnswerRepository,
        AttemptAnswerService attemptAnswerService
    ) {
        this.examAttemptRepository=examAttemptRepository;
        this.attemptAnswerRepository=attemptAnswerRepository;
        this.attemptAnswerService=attemptAnswerService;
    }

    @GetMapping("/{id}")
    public ResponseEntity getAttempt(@PathVariable Long id) {
        ExamAttempt a = examAttemptRepository.findById(id).orElse(null);
        if (a==null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(a);
        }

        return ResponseEntity.ok(a);
    }

    // Optional autosave of partial answers 
    @PutMapping("/{id}/answers")
    public ResponseEntity saveAnswers(@PathVariable Long id, @RequestBody SaveAnswersRequestDto body) {
        List<PartialAnswerDto> answers = body.answers();

        return ResponseEntity.ok("Success saving answers");
    }

    // Submit final answers, grade, and return the result
    @PostMapping("/{id}/submit")
    public String submitAttempt(@PathVariable Long id) {
        return "Success submitting attempt and return result";
    }

    // Result with per-question breakdown, only after submission
    @GetMapping("/{id}/result")
    public ResponseEntity getAttemptResult(@PathVariable Long id) {
        ExamAttempt attempt = examAttemptRepository.findById(id).orElse(null);
        if (attempt==null) {
            Map<String, Object> resBody = Map.of(
                "message","Invalid attempt id or exam"
            );

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resBody);
        };

        List<AttemptAnswer> answers = attemptAnswerService.getAnswersForAttempt(id);
        List<AnswerForResultDto> answersBreakDown = answers.stream()
        .map(a -> new AnswerForResultDto(
            a.getQuestion().getText(),
            a.getMarksAwarded(),
            new QuestionOptionForResultDto(a.getSelectedOption().getText(), a.getIsCorrect(), a.getSelectedOption().getLabel()),
            a.getQuestion().getOptions().stream().map(o -> new QuestionOptionForResultDto(o.getText(), o.getIsCorrect(), o.getLabel())).toList()
        ))
        .toList();

        AttemptResultDto result = new AttemptResultDto(
            attempt.getExam(),
            new StudentDto(attempt.getStudent().getFullname(), attempt.getStudent().getEmail()),
            answersBreakDown,
            attempt.getStatus(),
            attempt.getStartedAt(),
            attempt.getExpiresAt(),
            attempt.getSubmittedAt(),
            attempt.getUpdatedAt(),
            attempt.getScore(),
            attempt.getCorrectCount(),
            attempt.getWrongCount(),
            attempt.getUnansweredCount()
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/analytics/topics")
    public ResponseEntity getAttemptAnalytics(@PathVariable Long id) {
        ExamAttempt attempt = examAttemptRepository.findById(id).orElse(null);
        if (attempt==null) {
            Map<String, Object> resBody = Map.of(
                "message","Invalid attempt id or exam"
            );

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resBody);
        };

        List<AttemptAnswer> answers = attemptAnswerService.getAnswersForAttempt(id);
        List<Topic> topicsCovered = new ArrayList<>();
        for (AttemptAnswer a:answers) {
            Topic questionTopic = a.getQuestion().getTopic();
            if (!topicsCovered.contains(questionTopic)) {
                topicsCovered.add(questionTopic);
            }
        }

        List<TopicBreakdownForAnalyticsDto> topicsBreakdown = new ArrayList<>();
        for (Topic t:topicsCovered) {
            List<AnswerForResultDto> topicAnswers = new ArrayList<>();
            Integer wrong_count = 0;
            Integer correct_count = 0;
            Integer totalMark = 0;

            for (AttemptAnswer a:answers) {
                if (a.getQuestion().getTopic().getId()==t.getId()) {

                    Boolean isCorrect = a.getIsCorrect();
                    if (isCorrect) {
                        correct_count++;
                    } else {
                        wrong_count++;
                    }

                    Integer mark = a.getMarksAwarded();
                    totalMark=totalMark+mark;

                    topicAnswers.add(new AnswerForResultDto(
                        a.getQuestion().getText(),
                        a.getMarksAwarded(),
                        new QuestionOptionForResultDto(a.getSelectedOption().getText(), a.getIsCorrect(), a.getSelectedOption().getLabel()),
                        a.getQuestion().getOptions().stream().map(o -> new QuestionOptionForResultDto(o.getText(), o.getIsCorrect(), o.getLabel())).toList()
                    ));
                }
            }

            TopicBreakdownForAnalyticsDto topicBreakdown = new TopicBreakdownForAnalyticsDto(
                t.getName(),
                t.getDescription(),
                topicAnswers,
                wrong_count,
                correct_count,
                totalMark
            );

            topicsBreakdown.add(topicBreakdown);
        };

        AttemptAnalyticsDto analytics = new AttemptAnalyticsDto(
            attempt.getExam(),
            new StudentDto(attempt.getStudent().getFullname(), attempt.getStudent().getEmail()),
            topicsBreakdown,
            attempt.getStatus(),
            attempt.getStartedAt(),
            attempt.getExpiresAt(),
            attempt.getSubmittedAt(),
            attempt.getUpdatedAt(),
            attempt.getScore(),
            attempt.getCorrectCount(),
            attempt.getWrongCount(),
            attempt.getUnansweredCount()
        );

        return ResponseEntity.ok(analytics);
    }
}