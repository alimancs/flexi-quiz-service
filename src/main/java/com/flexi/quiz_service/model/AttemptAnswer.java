import jakarta.persistence.*;

@Entity
@Table(name="attemptanswers", schema="attemptanswer")
public class AttemptAnswer {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private Long attempt_id;
    private Long question_id;

    @Column(nullable=true)
    private Long selected_option_id;

    private Boolean is_correct;
    private Integer marks_awarded;

    public AttemptAnswer(){};
    public AttemptAnswer(
        Long attempt_id,
        Long question_id,
        Boolean is_correct,
        Integer marks_awarded
    ){
        this.attempt_id=attempt_id;
        this.question_id=question_id;
        this.is_correct=is_correct;
        this.marks_awarded=marks_awarded;
    }
    public AttemptAnswer(
        Long attempt_id,
        Long question_id,
        Boolean is_correct,
        Integer marks_awarded,
        Long selected_option_id
    ){
        this.attempt_id=attempt_id;
        this.question_id=question_id;
        this.is_correct=is_correct;
        this.marks_awarded=marks_awarded;
        this.selected_option_id=selected_option_id;
    }

    public Long getId(){ return id; };

    public Long getAttemptId(){ return attempt_id; };
    public void setAttemptId(Long newAttemptId){ this.attempt_id=newAttemptId; };

    public Long getQuestionId(){ return question_id; };
    public void setQuestionid(Long newQuestionId){ this.question_id=newQuestionId; };

    public Boolean getIsCorrect(){ return is_correct; };
    public void setIsCorrect(Boolean newIsCorrect){ this.is_correct=newIsCorrect; };

    public Integer getMarksAwarded(){ return marks_awarded; };
    public void setMarksAwarded(Integer newMarksAwarded){ this.marks_awarded=newMarksAwarded; };

    public Long getSelectedOptionId(){ return selected_option_id; };
    public void setSelectedOptionId(Long newSelectedOptionId){ this.selected_option_id=newSelectedOptionId; };

}