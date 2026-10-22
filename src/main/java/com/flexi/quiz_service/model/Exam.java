import jakarta.persistence.*;
import java.time.Instant;

enum ExamStatus {
    DRAFT,
    PUBLISHED,
    CLOSED
}

@Entity
@Table(name="exams", schema="exam")
public class Exam {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String title;
    private Integer duration_seconds;
    private Instant opens_at;
    private Instant closes_at;
    private Integer max_attempts;
    private Boolean negative_marking;
    private Integer penalty_per_wrong;
    private ExamStatus status;
    private String created_by;
    private Instant created_at;

    public Exam(){};
    public Exam(
        String title, 
        Integer duration_seconds, 
        Instant opens_at, 
        Instant closes_at, 
        Integer max_attempts, 
        Boolean negative_marking,
        Integer penalty_per_wrong,
        ExamStatus status,
        String created_by
        ) {
            this.title=title;
            this.duration_seconds=duration_seconds;
            this.opens_at=opens_at;
            this.closes_at=closes_at;
            this.max_attempts=max_attempts;
            this.negative_marking=negative_marking;
            this.penalty_per_wrong=penalty_per_wrong;
            this.status=status;
            this.created_by=created_by;
    };

    @PrePersist
    protected void onCreate() {
        created_at=Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String newTitle) {
        this.title = newTitle;
    }

    public Integer getDurationSeconds() {
        return duration_seconds;
    }
    public void setDurationSeconds(Integer newDurationSeconds) {
        this.duration_seconds = newDurationSeconds;
    }

    public Instant getOpensAt() {
        return opens_at;
    }
    public void setOpensAt(Instant newOpensAt) {
        this.opens_at = newOpensAt;
    }

    public Instant getClosesAt() {
        return closes_at;
    }
    public void setClosesAt(Instant newClosesAt) {
        this.closes_at = newClosesAt;
    }

    public Integer getMaxAttempts() {
        return max_attempts;
    }
    public void setMaxAttempts(Integer newMaxAttempts) {
        this.max_attempts= newMaxAttempts;
    }

    public Boolean getNegativeMarking() {
        return negative_marking;
    }
    public void setNegativeMarking(Boolean newNegativeMarking) {
        this.negative_marking=newNegativeMarking;
    }

    public Integer getPenaltyPerWrong() {
        return penalty_per_wrong;
    }
    public void setPenaltyPerWrong(Integer newPenaltyPerWrong) {
        this.penalty_per_wrong=newPenaltyPerWrong;
    }

    public ExamStatus getExamStatus() {
        return status;
    }
    public void setExamStatus(ExamStatus newStatus) {
        this.status=newStatus;
    }

    public String getCreatedBy() {
        return created_by;
    }
    public void setCreatedBy(String newCreatedBy) {
        this.created_by=newCreatedBy;
    }

    public Instant getCreatedAt() {
        return created_at;
    }
}