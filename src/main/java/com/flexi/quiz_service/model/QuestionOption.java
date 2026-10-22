import jakarta.persistence.*;

enum OptionLabel {
    A,
    B,
    C,
    D
}

@Entity
@Table(name="questionoptions", schema="questionoption")
public class QuestionOption {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private Long question_id;
    private String text;
    private Boolean is_correct;
    private OptionLabel label;

    public QuestionOption(){};
    public QuestionOption(Long question_id, String text, Boolean is_correct, OptionLabel label) {
        this.question_id=question_id;
        this.text=text;
        this.is_correct=is_correct;
        this.label=label;
    }

    public Long getId() {
        return id;
    }

    public Long getQuestionId() {
        return question_id;
    }
    public void setQuestionId(Long newQuestionId) {
        this.question_id=newQuestionId;
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