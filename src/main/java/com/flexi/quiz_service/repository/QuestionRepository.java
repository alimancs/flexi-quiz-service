package com.flexi.quiz_service.repository;

import com.flexi.quiz_service.model.MainQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<MainQuestion, Long>{
    Page<MainQuestion>findByTopic_Id(Long topicId, Pageable pageable);
    Page<MainQuestion>findByTopicIdAndText(Long topicId, String text, Pageable pageable);
};