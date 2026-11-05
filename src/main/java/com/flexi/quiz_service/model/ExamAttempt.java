package com.flexi.quiz_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.Instant;

enum AttemptStatus {
    IN_PROGRESS,
    SUBMITTED,
    EXPIRED
}

@Entity
@Table(name="attempts", schema="attempt")
public class ExamAttempt {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="exam_id", nullable=false)
    @JsonIgnore
    private Exam exam;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="student_id", nullable=false)
    @JsonIgnore
    private Student student;
    
    @Column(unique=true, nullable=false)
    private String token_hash;

    private AttemptStatus status=AttemptStatus.IN_PROGRESS;
    private Instant started_at;
    private Instant expires_at;
    private Instant submitted_at;
    private Instant updated_at;
    private Integer score=0;
    private Integer correct_count=0;
    private Integer wrong_count=0;
    private Integer unanswered_count=0;

    public ExamAttempt(){};
    public ExamAttempt(
        Exam exam,
        Student student,
        String token_hash,
        Instant expires_at

    ){
        this.exam=exam;
        this.student=student;
        this.token_hash=token_hash;
        this.expires_at=expires_at;
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        started_at=now;
        updated_at=now;
    }

    @PreUpdate
    protected void onUpdate() {
        updated_at=Instant.now();
    }

    public Long getId(){ return id; };

    public Exam getExam(){ return exam; };
    public void setExam(Exam newExam){ this.exam=newExam; };

    public Student getStudent(){ return student; };
    public void setStudent(Student newStudent){ this.student=newStudent; };

    public String getTokenHash(){ return token_hash; };
    public void setTokenHash(String newTokenHash){ this.token_hash=newTokenHash; };

    public AttemptStatus getStatus(){ return status; };
    public void setStatus(AttemptStatus newStatus){ this.status=newStatus; };

    public Instant getStartedAt(){ return started_at; };
    public void setStartedAt(Instant newStartedAt){ this.started_at=newStartedAt; };

    public Instant getExpiresAt(){ return expires_at; };
    public void setExpiresAt(Instant newExpiresAt){ this.expires_at=newExpiresAt; };

    public Instant getSubmittedAt(){ return submitted_at; };
    public void setSubmittedAt(Instant newSubmittedAt){ this.submitted_at=newSubmittedAt; };

    public Instant getUpdatedAt(){ return updated_at; };

    public Integer getScore(){ return score; };
    public void setScore(Integer newScore){ this.score=newScore; };

    public Integer getCorrectCount(){ return correct_count; };
    public void setCorrectCount(Integer newCorrectCount){ this.correct_count=newCorrectCount; };

    public Integer getWrongCount(){ return wrong_count; };
    public void setWrongCount(Integer newWrongCount){ this.wrong_count=newWrongCount; };

    public Integer getUnansweredCount(){ return unanswered_count; };
    public void setUnansweredCount(Integer newUnansweredCount){ this.unanswered_count=newUnansweredCount; };

}