package com.flexi.quiz_service.controller;

import com.flexi.quiz_service.model.Student;
import com.flexi.quiz_service.repository.StudentRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
public class StudentsController {
    private final StudentRepository studentRepository;

    public StudentsController(StudentRepository studentRepository) {
        this.studentRepository=studentRepository;
    }

    @GetMapping("/{id}")
    public Student getStudent(@PathVariable Long id) {
        Student s = studentRepository.findById(id).orElse(null);
        return s;
    }

    @GetMapping("/{id}/attempts")
    public String getStudentAttempts(@PathVariable String id) {
        return "Success getting user attempts";
    }

    @PostMapping
    public Student createStudent(@RequestBody Student req) {
        Student newStudent = new Student(req.getFullname(), req.getPassword(), req.getEmail());
        return studentRepository.save(newStudent);
    }

    
}