package com.flexi.quiz_service.controller;

import com.flexi.quiz_service.model.Topic;
import com.flexi.quiz_service.repository.TopicRepository;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;

@RestController
@RequestMapping("/topics")
public class TopicsController {

    private final TopicRepository topicRepository;

    public TopicsController(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    @GetMapping
    public ResponseEntity getTopics() {
        List<Topic> topics = topicRepository.findAll();
        Map<String, Object> resBody = Map.of(
            "topics", topics
        );
        return ResponseEntity.ok(resBody);
    }

    @PostMapping
    public ResponseEntity createTopic(@RequestBody Topic req) {
        
        Topic newTopic = new Topic(req.getName(), req.getDescription());
        Topic nt =  topicRepository.save(newTopic);
        Map<String, Object> resBody = Map.of(
            "topic", nt
        );
        return ResponseEntity.ok(resBody);
    }

    @PutMapping("/{id}/name")
    public ResponseEntity updateTopicName(@PathVariable Long id, @RequestBody JsonNode body ) {
        JsonNode newNameNode = body.get("newName");
        if (newNameNode==null || newNameNode.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid name update");
        };
        String newName = newNameNode.asString();
        
        Topic t = topicRepository.findById(id).orElse(null);
        if (t==null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Topic not or Invalid topic id");
        }
        t.setName(newName);
        topicRepository.save(t);
        Map<String, Object> resBody = Map.of(
            "topic", t
        );
        return ResponseEntity.ok(resBody);
    }

    @PutMapping("/{id}/desc")
    public ResponseEntity updateTopicDesc(@PathVariable Long id, @RequestBody JsonNode body ) {
        JsonNode newDescNode = body.get("newDesc");
        if (newDescNode==null || newDescNode.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid name update");
        };
        String newDesc = newDescNode.asString();
        
        Topic t = topicRepository.findById(id).orElse(null);
        if (t==null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Topic not or Invalid topic id");
        }
        t.setDescription(newDesc);
        topicRepository.save(t);
        Map<String, Object> resBody = Map.of(
            "topic", t
        );
        return ResponseEntity.ok(resBody);
    }
}