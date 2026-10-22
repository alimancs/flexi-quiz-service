import jakarta.persistence.*;


@Entity
@Table(name="students", schema="student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullname;
    private String password;
    private String email;

    public Student() {};

    public Student(String fullname, String password, String email) {
        this.fullname=fullname;
        this.email=email;
        this.password=password;
    }  

    public Long getId() {
        return id;
    }
    
    public String getFullname() {
        return fullname;
    }
    public void setFullname(String newFullname){
        this.fullname=newFullname;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String newPassword) {
        this.password=newPassword;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String newEmail) {
        this.email=email;
    } 
}