package com.flexi.quiz_service.controller;

import com.flexi.quiz_service.model.Topic;
import com.flexi.quiz_service.repository.TopicRepository;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/topics")
public class TopicsController {

    private final TopicRepository topicRepository;

    public TopicsController(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    @GetMapping
    public List<Topic> getTopics() {
        List<Topic> topics = topicRepository.findAll();
        return topics;
    }

    @PostMapping
    public Topic createTopic(@RequestBody Topic req) {
        
        Topic newTopic = new Topic(req.getName(), req.getDescription());
        return topicRepository.save(newTopic);
    }
}