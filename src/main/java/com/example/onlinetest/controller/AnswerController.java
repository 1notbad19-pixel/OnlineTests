package com.example.onlinetest.controller;

import com.example.onlinetest.dto.AnswerRequest;
import com.example.onlinetest.dto.AnswerResponse;
import com.example.onlinetest.service.AnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/answers")
@RequiredArgsConstructor
public class AnswerController {

  private final AnswerService answerService;

  @PostMapping
  public ResponseEntity<AnswerResponse> createAnswer(@Valid @RequestBody AnswerRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(answerService.createAnswer(request));
  }

  @GetMapping("/{id}")
  public ResponseEntity<AnswerResponse> getAnswer(@PathVariable Long id) {
    return ResponseEntity.ok(answerService.getAnswer(id));
  }

  @GetMapping
  public ResponseEntity<List<AnswerResponse>> getAllAnswers() {
    return ResponseEntity.ok(answerService.getAllAnswers());
  }

  @GetMapping("/question/{questionId}")
  public ResponseEntity<List<AnswerResponse>> getAnswersByQuestionId(@PathVariable Long questionId) {
    return ResponseEntity.ok(answerService.getAnswersByQuestionId(questionId));
  }

  @PutMapping("/{id}")
  public ResponseEntity<AnswerResponse> updateAnswer(@PathVariable Long id, @Valid @RequestBody AnswerRequest request) {
    return ResponseEntity.ok(answerService.updateAnswer(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAnswer(@PathVariable Long id) {
    answerService.deleteAnswer(id);
    return ResponseEntity.noContent().build();
  }
}