package com.flexi.quiz_service.repository;

import com.flexi.quiz_service.model.ExamQuestion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, Long>{
    @Query("""
        SELECT DISTINCT eq
        FROM ExamQuestion eq
        JOIN FETCH eq.question q
        LEFT JOIN FETCH q.options o
        WHERE eq.exam.id = :examId
            AND (q.active IS NULL or q.active=true)
            ORDER BY eq.id ASC, q.id ASC , o.label ASC
        """)
    List<ExamQuestion> findForExam(@Param("examId") Long examId);

    @Query("SELECT COALESCE(MAX(eq.position), 0) + 1 FROM ExamQuestion eq WHERE eq.exam.id = :examId")
    int nextPosition(@Param("examId") Long examId);
};