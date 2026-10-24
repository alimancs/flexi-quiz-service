import jakarta.persistence.*;
import net.minidev.json.annotate.JsonIgnore;

@Entity
@Table(name="attempt_answers", schema="attemptanswer")
public class AttemptAnswer {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="attempt_id", nullable=false)
    @JsonIgnore
    private Attempt attempt;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="question_id", nullable=false)
    @JsonIgnore
    private Question question;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="selected_option_id", nullable=true)
    @JsonIgnore
    private QuestionOption selected_option;

    private Boolean is_correct;
    private Integer marks_awarded;

    public AttemptAnswer(){};
    public AttemptAnswer(
        Attempt attempt,
        Question question,
        Boolean is_correct,
        Integer marks_awarded
    ){
        this.attempt=attempt;
        this.question=question;
        this.is_correct=is_correct;
        this.marks_awarded=marks_awarded;
    }
    public AttemptAnswer(
        Attempt attempt,
        Question question,
        Boolean is_correct,
        Integer marks_awarded,
        QuestionOption selected_option
    ){
        this.attempt=attempt;
        this.question=question;
        this.is_correct=is_correct;
        this.marks_awarded=marks_awarded;
        this.selected_option=selected_option;
    }

    public Long getId(){ return id; };

    public Attempt getAttempt(){ return attempt; };
    public void setAttemptId(Attempt newAttempt){ this.attempt=newAttempt; };

    public Question getQuestion(){ return question; };
    public void setQuestionid(Question newQuestion){ this.question=newQuestion; };

    public Boolean getIsCorrect(){ return is_correct; };
    public void setIsCorrect(Boolean newIsCorrect){ this.is_correct=newIsCorrect; };

    public Integer getMarksAwarded(){ return marks_awarded; };
    public void setMarksAwarded(Integer newMarksAwarded){ this.marks_awarded=newMarksAwarded; };

    public QuestionOption getSelectedOptionId(){ return selected_option; };
    public void setSelectedOptionId(QuestionOption newSelectedOption){ this.selected_option=newSelectedOption; };

}