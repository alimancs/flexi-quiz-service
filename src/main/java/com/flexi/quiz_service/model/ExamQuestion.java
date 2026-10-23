import jakarta.persistence.*;

@Entity
@Table(name="examquestions", schema="examquestion")
public class ExamQuestion {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable=false)
    private Long exam_id;

    @Column(nullable=false)
    private Long question_id;

    private Integer position;
    private Integer marks = 1;

    public ExamQuestion(){};
    public ExamQuestion(Long exam_id, Long question_id, Integer position) {
        this.exam_id=exam_id;
        this.question_id=question_id;
        this.position=position;
    }
    public ExamQuestion(Long exam_id, Long question_id, Integer position, Integer marks) {
        this.exam_id=exam_id;
        this.question_id=question_id;
        this.position=position;
        this.marks=marks;
    }

    public Long getId(){ return id; };

    public Long getExamId(){ return exam_id; };
    public void setExamId(Long newExamId){ this.exam_id=newExamId; };

    public Long getQuestionId(){ return question_id; };
    public void setQuestionId(Long newQuestionId){ this.question_id=newQuestionId; };

    public Integer getPosition(){ return position; };
    public void setPosition(Integer newPosition){ this.position=newPosition; };

    public Integer getMarks(){ return marks; };
    public void setMarks(Integer newMarks){ this.marks=newMarks; };
}