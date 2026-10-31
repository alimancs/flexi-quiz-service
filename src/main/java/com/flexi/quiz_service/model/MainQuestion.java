package com.flexi.quiz_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexi.quiz_service.types.QuestionDifficulty;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;



@Entity
@Table(name="questions", schema="question")
public class MainQuestion {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable=true)
    private QuestionDifficulty difficulty;
    
    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="topic_id", nullable=false)
    @JsonIgnore
    private Topic topic;

    private String text;
    private Boolean active;

    @Column(nullable=false, updatable=false)
    private Instant created_at;

    @Column(nullable=false)
    private Instant updated_at;

    @OneToMany(mappedBy="question", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<MainQuestionOption> options = new ArrayList<>();
    
    public MainQuestion(){};
    public MainQuestion(Topic topic, String text) {
        this.topic=topic;
        this.text=text;
    }
    public MainQuestion(Topic topic, String text, QuestionDifficulty difficulty) {
        this.topic=topic;
        this.text=text;
        this.difficulty=difficulty;
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

    public QuestionDifficulty getDifficulty() {
        return difficulty;
    }
    public void setDifficulty(QuestionDifficulty newDifficulty) {
        this.difficulty=newDifficulty;
    }

    public Boolean getActive() {
        return active;
    }
    public void setActive(Boolean newActive) {
        this.active = newActive;
    }

    public void addOptions(MainQuestionOption option) {
        options.add(option);
        option.setQuestion(this);
    }
    public List<MainQuestionOption> getOptions() {
        return options;
    }
}