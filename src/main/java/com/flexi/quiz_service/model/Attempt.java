import jakarta.persistence.*;
import java.time.Instant;

enum AttemptStatus {
    IN_PROGRESS,
    SUBMITTED,
    EXPIRED
}

@Entity
@Table(name="attempts", schema="attempt")
public class Attempt {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable=false)
    private Long exam_id;

    @Column(nullable=false)
    private Long student_id;
    
    @Column(unique=true, nullable=false)
    private String token_hash;

    private AttemptStatus status=AttemptStatus.IN_PROGRESS;
    private Instant started_at;
    private Instant expires_at;
    private Instant submitted_at;
    private Instant updated_at;
    private Integer score;
    private Integer max_score;
    private Integer correct_count;
    private Integer wrong_count;
    private Integer unanswered_count;

    public Attempt(){};
    public Attempt(
        Long exam_id,
        Long student_id,
        String token_hash,
        Instant expires_at,
        Integer score,
        Integer max_score,
        Integer correct_count,
        Integer wrong_count,
        Integer unanswered_count
    ){
        this.exam_id=exam_id;
        this.student_id=student_id;
        this.token_hash=token_hash;
        this.expires_at=expires_at;
        this.score=score;
        this.max_score=max_score;
        this.correct_count=correct_count;
        this.wrong_count=wrong_count;
        this.unanswered_count=unanswered_count;
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

    public Long getExamId(){ return exam_id; };
    public void setExamId(Long newExamId){ this.exam_id=newExamId; };

    public Long getStudentId(){ return student_id; };
    public void setStudentId(Long newStudentId){ this.student_id=newStudentId; };

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

    public Integer getMaxScore(){ return max_score; };
    public void setMaxScore(Integer newMaxScore){ this.max_score=newMaxScore; };

    public Integer getWrongCount(){ return wrong_count; };
    public void setWrongCount(Integer newWrongCount){ this.wrong_count=newWrongCount; };

    public Integer getUnansweredCount(){ return unanswered_count; };
    public void setUnansweredCount(Integer newUnansweredCount){ this.unanswered_count=newUnansweredCount; };

}