

import jakarta.persistence.*;
import net.minidev.json.annotate.JsonIgnore;

enum OptionLabel {
    A,
    B,
    C,
    D
}

@Entity
@Table(name="question_options", schema="questionoption")
public class QuestionOption {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="question_id", nullable=false)
    @JsonIgnore
    private Question question;

    private String text;
    private Boolean is_correct;
    private OptionLabel label;

    public QuestionOption(){};
    public QuestionOption(Question question, String text, Boolean is_correct, OptionLabel label) {
        this.question=question;
        this.text=text;
        this.is_correct=is_correct;
        this.label=label;
    }

    public Long getId() {
        return id;
    }

    public Question getQuestion() {
        return question;
    }
    public void setQuestion(Question newQuestion) {
        this.question=newQuestion;
    }

    public String getText() {
        return text;
    }
    public void setText(String newText) {
        this.text=newText;
    }

    public OptionLabel getLabel() {
        return label;
    }
    public void setLabel(OptionLabel newLabel) {
        this.label=newLabel;
    }

    public Boolean getIsCorrect() {
        return is_correct;
    }
    public void setIsCorrect(Boolean newIsCorrect) {
        this.is_correct=newIsCorrect;
    }
}