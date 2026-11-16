package com.flexi.quiz_service.service;


import com.flexi.quiz_service.dto.OptionForExamDto;
import com.flexi.quiz_service.dto.QuestionForExamDto;
import com.flexi.quiz_service.model.Exam;
import com.flexi.quiz_service.model.ExamQuestion;
import com.flexi.quiz_service.model.MainQuestion;
import com.flexi.quiz_service.model.MainQuestionOption;
import com.flexi.quiz_service.repository.ExamQuestionRepository;
import com.flexi.quiz_service.repository.ExamRepository;
import com.flexi.quiz_service.repository.QuestionOptionRepository;
import com.flexi.quiz_service.repository.QuestionRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ExamQuestionService {

    private final ExamQuestionRepository examQuestionRepository;
    private final ExamRepository examRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final QuestionRepository questionRepository;

    public ExamQuestionService(
        ExamQuestionRepository examQuestionRepository,
        ExamRepository examRepository, 
        QuestionOptionRepository questionOptionRepository,
        QuestionRepository questionRepository
        ) {
        this.examQuestionRepository=examQuestionRepository;
        this.examRepository=examRepository;
        this.questionOptionRepository=questionOptionRepository;
        this.questionRepository=questionRepository;
    }

    @Transactional(readOnly=true)
    public List<QuestionForExamDto> getQuestionsForExam(Long examId) {
        List<ExamQuestion> rows = examQuestionRepository.findForExam(examId);
        if (rows.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid exam or no questions found.");
        };

        return rows.stream().map(ExamQuestionService::examQuestionToDto).toList();
    }

    public QuestionForExamDto addQuestionToExam(
        Long examId, 
        MainQuestion question, 
        List<MainQuestionOption> options 
        ) {
        Exam e = examRepository.findById(examId).orElseThrow(()->new RuntimeException("Exam with id: "+examId+" not found"));


        MainQuestion sq = questionRepository.save(question);
        
        options.stream().map(o -> questionOptionRepository.save(o)).forEach(sq::addOptions);
        questionRepository.save(sq);

        Integer position = examQuestionRepository.nextPosition(examId);
        ExamQuestion eq = new ExamQuestion(e, sq, position);
        examQuestionRepository.save(eq);
        return examQuestionToDto(eq);
    }

    private static QuestionForExamDto examQuestionToDto(ExamQuestion examQuestion) {
        MainQuestion eq = examQuestion.getQuestion();
        List<OptionForExamDto> opts = eq.getOptions()
        .stream()
        .map( o -> new OptionForExamDto(o.getId(), o.getLabel().name(), o.getText()))
        .toList();

        return new QuestionForExamDto(
            eq.getId(), 
            examQuestion.getId(), 
            eq.getText(), 
            eq.getDifficulty() == null ? null:eq.getDifficulty(), 
            examQuestion.getPosition(),
            examQuestion.getMarks(), 
            opts
            );
    }

}