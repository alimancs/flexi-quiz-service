package com.flexi.quiz_service.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;


@Entity
@Table(name="attempt_answers", schema="attemptanswer")
public class AttemptAnswer {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="attempt_id", nullable=false)
    @JsonIgnore
    private ExamAttempt attempt;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="question_id", nullable=false)
    @JsonIgnore
    private MainQuestion question;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="selected_option_id", nullable=true)
    @JsonIgnore
    private MainQuestionOption selected_option;

    private Boolean is_correct;
    private Integer marks_awarded;

    public AttemptAnswer(){};
    public AttemptAnswer(
        Boolean is_correct,
        Integer marks_awarded
    ){
        this.is_correct=is_correct;
        this.marks_awarded=marks_awarded;
    }
    public AttemptAnswer(
        ExamAttempt attempt,
        MainQuestion question,
        Boolean is_correct,
        Integer marks_awarded
    ){
        this.attempt=attempt;
        this.question=question;
        this.is_correct=is_correct;
        this.marks_awarded=marks_awarded;
    }
    public AttemptAnswer(
        ExamAttempt attempt,
        MainQuestion question,
        Boolean is_correct,
        Integer marks_awarded,
        MainQuestionOption selected_option
    ){
        this.attempt=attempt;
        this.question=question;
        this.is_correct=is_correct;
        this.marks_awarded=marks_awarded;
        this.selected_option=selected_option;
    }

    public Long getId(){ return id; };

    public ExamAttempt getAttempt(){ return attempt; };
    public void setAttemptId(ExamAttempt newAttempt){ this.attempt=newAttempt; };

    public MainQuestion getQuestion(){ return question; };
    public void setQuestionid(MainQuestion newQuestion){ this.question=newQuestion; };

    public Boolean getIsCorrect(){ return is_correct; };
    public void setIsCorrect(Boolean newIsCorrect){ this.is_correct=newIsCorrect; };

    public Integer getMarksAwarded(){ return marks_awarded; };
    public void setMarksAwarded(Integer newMarksAwarded){ this.marks_awarded=newMarksAwarded; };

    public MainQuestionOption getSelectedOption(){ return selected_option; };
    public void setSelectedOptionId(MainQuestionOption newSelectedOption){ this.selected_option=newSelectedOption; };

}