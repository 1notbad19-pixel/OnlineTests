package com.example.onlinetest.controller;

import com.example.onlinetest.dto.QuestionRequest;
import com.example.onlinetest.dto.QuestionResponse;
import com.example.onlinetest.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

  private final QuestionService questionService;

  @PostMapping
  public ResponseEntity<QuestionResponse> createQuestion(@Valid @RequestBody QuestionRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(questionService.createQuestion(request));
  }

  @GetMapping("/{id}")
  public ResponseEntity<QuestionResponse> getQuestion(@PathVariable Long id) {
    return ResponseEntity.ok(questionService.getQuestion(id));
  }

  @GetMapping
  public ResponseEntity<List<QuestionResponse>> getAllQuestions() {
    return ResponseEntity.ok(questionService.getAllQuestions());
  }

  @GetMapping("/quiz/{quizId}")
  public ResponseEntity<List<QuestionResponse>> getQuestionsByQuizId(@PathVariable Long quizId) {
    return ResponseEntity.ok(questionService.getQuestionsByQuizId(quizId));
  }

  @PutMapping("/{id}")
  public ResponseEntity<QuestionResponse> updateQuestion(@PathVariable Long id, @Valid @RequestBody QuestionRequest request) {
    return ResponseEntity.ok(questionService.updateQuestion(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
    questionService.deleteQuestion(id);
    return ResponseEntity.noContent().build();
  }
}