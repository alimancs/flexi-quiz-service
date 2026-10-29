package com.flexi.quiz_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name="exam_questions", schema="examquestion")
public class ExamQuestion {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="exam_id", nullable=false)
    @JsonIgnore
    private Exam exam;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="question_id", nullable=false)
    @JsonIgnore
    private MainQuestion question;

    private Integer position;
    private Integer marks = 1;

    public ExamQuestion(){};
    public ExamQuestion(Exam exam, MainQuestion question, Integer position) {
        this.exam=exam;
        this.question=question;
        this.position=position;
    }
    public ExamQuestion(Exam exam, MainQuestion question, Integer position, Integer marks) {
        this.exam=exam;
        this.question=question;
        this.position=position;
        this.marks=marks;
    }

    public Long getId(){ return id; };

    public Exam getExamId(){ return exam; };
    public void setExamId(Exam newExam){ this.exam=newExam; };

    public MainQuestion getQuestion(){ return question; };
    public void setQuestion(MainQuestion newQuestion){ this.question=newQuestion; };

    public Integer getPosition(){ return position; };
    public void setPosition(Integer newPosition){ this.position=newPosition; };

    public Integer getMarks(){ return marks; };
    public void setMarks(Integer newMarks){ this.marks=newMarks; };
}