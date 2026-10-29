package com.flexi.quiz_service.model;

import jakarta.persistence.*;

@Entity
@Table(name="teachers", schema="teacher")
public class Teacher {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String fullname;
    private String department;
    
    public Teacher(){};
    public Teacher(String fullname, String department) {
        this.fullname=fullname;
        this.department=department;
    }

    public Long getId(){return this.id;};

    public String getFullname(){return this.fullname;};
    public void setFullname(String newFullname){this.fullname=newFullname;};

    public String getDepartment(){return this.department;};
    public void setDepartment(String newDepartment){this.department=newDepartment;};
}