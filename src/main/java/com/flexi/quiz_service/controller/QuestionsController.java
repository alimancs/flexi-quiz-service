package com.flexi.quiz_service.controller;

import com.flexi.quiz_service.model.MainQuestion;
import com.flexi.quiz_service.repository.QuestionRepository;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/questions")
public class QuestionsController {
    private final QuestionRepository questionRepository;

    public QuestionsController(QuestionRepository questionRepository) {
        this.questionRepository=questionRepository;
    }
    
    // Get one question by id
    @GetMapping("{id}")
    public ResponseEntity<MainQuestion> getQuestion(@PathVariable Long id) {
        MainQuestion q = questionRepository.findById(id).orElseThrow(()-> new RuntimeException("Question with id: "+id+" not found"));
        return ResponseEntity.ok(q);
    }
    
    // get questions by topic
    @GetMapping
    public ResponseEntity<Page<MainQuestion>> getQuestions(
        @RequestParam Long topicId, 
        @RequestParam(required=false) String q, 
        @RequestParam(defaultValue="0") Integer page,
        @RequestParam(defaultValue="5") Integer size
        ) {

            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

            if (q!=null && !q.isEmpty()) {
                return ResponseEntity.ok(questionRepository.findByTopic_IdAndText(topicId, q, pageable));
            }

            return ResponseEntity.ok(questionRepository.findByTopic_Id(topicId, pageable));
    }

    // Update question
    @PutMapping("{id}")
    public ResponseEntity<MainQuestion> updateQuestion(@PathVariable Long id, @RequestBody String text) {
        MainQuestion q = questionRepository.findById(id).orElseThrow(()->new RuntimeException("question: "+id+" not found"));
        q.setText(text);
        questionRepository.save(q);
        return ResponseEntity.ok(q);
    }

    @PostMapping
    public ResponseEntity<MainQuestion> createQuestion(@RequestBody MainQuestion newQuestion) {
        MainQuestion nq = new MainQuestion(newQuestion.getTopic(), newQuestion.getText());
        questionRepository.save(nq);
        return ResponseEntity.ok(nq);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteQuestion(@PathVariable Long id) {
        questionRepository.deleteById(id);
        return ResponseEntity.ok("Success deleting question with id: "+id);
    }
}