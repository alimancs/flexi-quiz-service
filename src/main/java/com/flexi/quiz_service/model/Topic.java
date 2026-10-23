import jakarta.persistence.*;

@Entity
@Table(name="topics", schema="topic")

public class Topic {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true, nullable=false)
    private String name;

    private String description;

    public Topic(){};
    public Topic(String name, String description) {
        this.name=name;
        this.description=description;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public void setName(String newName) {
        this.name = newName;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String newDescription) {
        this.description = newDescription;
    }
}