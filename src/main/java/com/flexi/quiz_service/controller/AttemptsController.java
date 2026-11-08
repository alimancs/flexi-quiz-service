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
import com.flexi.quiz_service.model.Exam;
import com.flexi.quiz_service.model.ExamAttempt;
import com.flexi.quiz_service.model.ExamQuestion;
import com.flexi.quiz_service.model.MainQuestion;
import com.flexi.quiz_service.model.MainQuestionOption;
import com.flexi.quiz_service.model.Topic;
import com.flexi.quiz_service.repository.AttemptAnswerRepository;
import com.flexi.quiz_service.repository.ExamAttemptRepository;
import com.flexi.quiz_service.repository.ExamQuestionRepository;
import com.flexi.quiz_service.repository.QuestionOptionRepository;
import com.flexi.quiz_service.repository.QuestionRepository;
import com.flexi.quiz_service.service.AttemptAnswerService;
import com.flexi.quiz_service.types.AttemptStatus;
import java.time.Instant;
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
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final ExamQuestionRepository examQuestionRepository;

    private final AttemptAnswerService attemptAnswerService;

    public AttemptsController(
        ExamAttemptRepository examAttemptRepository,
        AttemptAnswerRepository attemptAnswerRepository,
        AttemptAnswerService attemptAnswerService,
        QuestionRepository questionRepository,
        QuestionOptionRepository questionOptionRepository,
        ExamQuestionRepository examQuestionRepository
    ) {
        this.examAttemptRepository=examAttemptRepository;
        this.attemptAnswerRepository=attemptAnswerRepository;
        this.attemptAnswerService=attemptAnswerService;
        this.questionRepository=questionRepository;
        this.questionOptionRepository=questionOptionRepository;
        this.examQuestionRepository=examQuestionRepository;
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
        if (answers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nothing to save");
        }
        ExamAttempt attempt = examAttemptRepository.findById(id).orElse(null);
        if (attempt==null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid exam attempt or not found");
        }

        List<PartialAnswerDto> invalidAnswers = new ArrayList<>();

        for (PartialAnswerDto partialAnswer:answers) {
            AttemptAnswer answer = attemptAnswerRepository.findForAttemptAndQuestion(id, partialAnswer.questionId()).orElse(null);
            MainQuestion question = questionRepository.findById(partialAnswer.questionId()).orElse(null);
            MainQuestionOption selectedOption = questionOptionRepository.findById(partialAnswer.selectedOptionId()).orElse(null);

            if (question==null || selectedOption==null) {
                invalidAnswers.add(partialAnswer);
                continue;
            }
            if (answer==null) {
                answer = new AttemptAnswer(attempt, question, selectedOption.getIsCorrect(), attempt.getExam().getScorePerQuestion());
            } else {
                answer.setIsCorrect(selectedOption.getIsCorrect());
                answer.setSelectedOption(selectedOption);
            }

            attemptAnswerRepository.save(answer);
        }

        Map<String, Object> resBody = Map.of(
            "invalidAnswers", invalidAnswers
        );

        return ResponseEntity.ok(resBody);
    }

    // Submit final answers, grade, and return the result
    @PostMapping("/{id}/submit")
    public ResponseEntity submitAttempt(@PathVariable Long id,  @RequestBody SaveAnswersRequestDto body) {
         Instant submitted_at = Instant.now();
        List<PartialAnswerDto> answers = body.answers();
        if (answers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nothing to save");
        }

        ExamAttempt attempt = examAttemptRepository.findById(id).orElse(null);
        if (attempt==null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid exam attempt or not found");
        }
    
        Exam exam = attempt.getExam();
        List<ExamQuestion> examQuestions = examQuestionRepository.findForExam(exam.getId());
        Integer total_questions = examQuestions.size();

        List<PartialAnswerDto> invalidAnswers = new ArrayList<>();

        for (PartialAnswerDto partialAnswer:answers) {
            AttemptAnswer answer = attemptAnswerRepository.findForAttemptAndQuestion(id, partialAnswer.questionId()).orElse(null);
            MainQuestion question = questionRepository.findById(partialAnswer.questionId()).orElse(null);
            MainQuestionOption selectedOption = questionOptionRepository.findById(partialAnswer.selectedOptionId()).orElse(null);

            if (question==null || selectedOption==null) {
                invalidAnswers.add(partialAnswer);
                continue;
            }
            if (answer==null) {
                answer = new AttemptAnswer(attempt, question, selectedOption.getIsCorrect(), attempt.getExam().getScorePerQuestion());
            } else {
                answer.setIsCorrect(selectedOption.getIsCorrect());
                answer.setSelectedOption(selectedOption);
            }

            attemptAnswerRepository.save(answer);
        }

        List<AttemptAnswer> savedAnswers = attemptAnswerService.getAnswersForAttempt(id);
        Integer correct_count = 0;
        Integer wrong_count = 0;
        for (AttemptAnswer ans:savedAnswers) {
            if (ans.getIsCorrect()) {
                correct_count++;
            } else {
                wrong_count++;
            }
        }

        Integer total_answered = savedAnswers.size();
        Integer score = exam.getScorePerQuestion() * correct_count;

        attempt.setUnansweredCount(total_questions-total_answered);
        attempt.setSubmittedAt(submitted_at);
        attempt.setStatus(AttemptStatus.SUBMITTED);
        attempt.setScore(score);
        attempt.setCorrectCount(correct_count);
        attempt.setWrongCount(wrong_count);
        examAttemptRepository.save(attempt);

        List<AnswerForResultDto> answersBreakDown = savedAnswers.stream()
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

        Map<String, Object> resBody = Map.of(
            "invalidAnswers", invalidAnswers,
            "result", result
        );

        return ResponseEntity.ok(resBody);
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