package com.flexi.quiz_service.repository;

import com.flexi.quiz_service.model.ExamAttempt;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long>{

    @Query("""
        SELECT DISTINCT ea
        FROM ExamAttempt ea
        JOIN FETCH ea.exam a
        WHERE ea.student.id = :studentId
        ORDER BY ea.id ASC, a.id ASC
    """)
    List<ExamAttempt> findForStudent(@Param("studentId") Long studentId);
    
    @Query("""
        SELECT DISTINCT ea
        FROM ExamAttempt ea
        JOIN FETCH ea.exam a
        WHERE ea.exam.id = :examId
        ORDER BY ea.score DESC, ea.id DESC
    """)
    List<ExamAttempt> findForExamDescendingByScore(@Param("examId") Long examId);
};