import jakarta.persistence.*;
import java.time.Instant;
import net.minidev.json.annotate.JsonIgnore;


enum Difficulty {
    EASY,
    AVERAGE,
    HARD
}

@Entity
@Table(name="questions", schema="question")
public class Question {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable=true)
    private Difficulty difficulty;
    
    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="topic_id", nullable=false)
    @JsonIgnore
    private Topic topic;

    private String text;
    private Boolean active;
    private String created_by;

    @Column(nullable=false, updatable=false)
    private Instant created_at;

    @Column(nullable=false)
    private Instant updated_at;
    
    public Question(){};
    public Question(Topic topic, String text, String created_by) {
        this.topic=topic;
        this.text=text;
        this.created_by=created_by;
    }
    public Question(Topic topic, String text, Difficulty difficulty, String created_by) {
        this.topic=topic;
        this.text=text;
        this.difficulty=difficulty;
        this.created_by=created_by;
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        created_at=now;
        updated_at=now;
    }

    @PreUpdate
    protected void onUpdate() {
        updated_at=Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Topic getTopic() {
        return topic;
    }
    public void setTopic(Topic newTopic) {
        this.topic=newTopic;
    }

    public String getText() {
        return text;
    }
    public void setText(String newText) {
        this.text=newText;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }
    public void setDifficulty(Difficulty newDifficulty) {
        this.difficulty=newDifficulty;
    }

    public String getCreatedBy() {
        return created_by;
    }
    public void setCreatedBy(String newCreatedBy) {
        this.created_by=newCreatedBy;
    }

    public Boolean getActive() {
        return active;
    }
    public void setActive(Boolean newActive) {
        this.active = newActive;
    }
}