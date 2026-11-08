package com.flexi.quiz_service.repository;

import com.flexi.quiz_service.model.AttemptAnswer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface AttemptAnswerRepository extends JpaRepository<AttemptAnswer, Long>{
    
    @Query("""
        SELECT DISTINCT aa
        FROM AttemptAnswer aa
        JOIN FETCH aa.question q
        LEFT JOIN FETCH aa.selected_option so
        WHERE aa.attempt.id = :attemptId
        ORDER BY aa.id ASC, q.id ASC, so.id ASC
    """)
    List<AttemptAnswer> findForAttempt(@Param("attemptId") Long attemptId);

    @Query("""
        SELECT aa
        FROM AttemptAnswer aa
        JOIN FETCH aa.question q
        LEFT JOIN FETCH aa.selected_option so
        WHERE aa.attempt.id = :attemptId
        AND q.id = :questionId
    """)
    Optional<AttemptAnswer> findForAttemptAndQuestion(@Param("attemptId") Long attemptId, @Param("questionId") Long questionId);
};