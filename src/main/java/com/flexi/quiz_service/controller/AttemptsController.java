package com.flexi.quiz_service.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/attempts")
public class AttemptsController {
    @GetMapping("/{id}")
    public String getAttempt(@PathVariable String id) {
        return "Success getting attempt";
    }

    // Optional autosave of partial answers 
    @PutMapping("/{id}/answers")
    public String saveAnswers(@PathVariable String id) {
        return "Success saving answers";
    }

    // Submit final answers, grade, and return the result
    @PostMapping("/{id}/submit")
    public String submitAttempt(@PathVariable String id) {
        return "Success submitting attempt and return result";
    }

    // Result with per-question breakdown, only after submission
    @GetMapping("/{id}/result")
    public String getAttemptResult(@PathVariable String id) {
        return "Success getting attempt result";
    }

    @GetMapping("/{id}/analytics/topics")
    public String getAttemptAnalytics(@PathVariable String id) {
        return "Success returning attempt analytics";
    }
}