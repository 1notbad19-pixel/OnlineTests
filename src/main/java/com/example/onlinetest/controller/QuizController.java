package com.example.onlinetest.controller;

import com.example.onlinetest.dto.QuizRequest;
import com.example.onlinetest.dto.QuizResponse;
import com.example.onlinetest.service.QuizService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @PostMapping
  public ResponseEntity<QuizResponse> createQuiz(@Valid @RequestBody QuizRequest request) {
        QuizResponse response = quizService.createQuiz(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
  public ResponseEntity<QuizResponse> getQuiz(@PathVariable Long id) {
        QuizResponse response = quizService.getQuiz(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
  public ResponseEntity<List<QuizResponse>> getQuizzes(
        @RequestParam(required = false) String category,
        @RequestParam(required = false) Boolean published,
        @RequestParam(required = false) String tag) {
        List<QuizResponse> responses = quizService.getAllQuizzes(category, published, tag);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
  public ResponseEntity<QuizResponse> updateQuiz(
        @PathVariable Long id,
        @Valid @RequestBody QuizRequest request) {
        QuizResponse response = quizService.updateQuiz(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }
}