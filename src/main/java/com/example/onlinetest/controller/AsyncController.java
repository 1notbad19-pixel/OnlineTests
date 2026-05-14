package com.example.onlinetest.controller;

import com.example.onlinetest.dto.QuizRequest;
import com.example.onlinetest.service.AsyncQuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/async")
@RequiredArgsConstructor
public class AsyncController {

  private final AsyncQuizService asyncQuizService;

  // ==================== АСИНХРОННОЕ СОЗДАНИЕ КВИЗА ====================

  @PostMapping("/quiz")
  public CompletableFuture<ResponseEntity<String>> createQuizAsync(@RequestBody QuizRequest request) {
    return asyncQuizService.createQuizAsync(request)
        .thenApply(taskId -> ResponseEntity.accepted().body(taskId));
  }

  @GetMapping("/status/{taskId}")
  public ResponseEntity<String> getTaskStatus(@PathVariable String taskId) {
    return ResponseEntity.ok(asyncQuizService.getTaskStatus(taskId));
  }

  // ==================== ЭНДПОИНТЫ ДЛЯ RACE CONDITION ====================

  @PostMapping("/race/unsafe")
  public ResponseEntity<String> incrementUnsafe() {
    asyncQuizService.incrementUnsafe();
    return ResponseEntity.ok("Unsafe counter: " + asyncQuizService.getUnsafeCounter());
  }

  @PostMapping("/race/safe")
  public ResponseEntity<String> incrementSafe() {
    asyncQuizService.incrementSafe();
    return ResponseEntity.ok("Safe counter: " + asyncQuizService.getSafeCounter());
  }

  @PostMapping("/race/synchronized")
  public ResponseEntity<String> incrementSynchronized() {
    asyncQuizService.incrementSynchronized();
    return ResponseEntity.ok("Synchronized counter: " + asyncQuizService.getSynchronizedCounter());
  }

  @GetMapping("/race/counters")
  public ResponseEntity<RaceConditionResult> getCounters() {
    return ResponseEntity.ok(new RaceConditionResult(
        asyncQuizService.getUnsafeCounter(),
        asyncQuizService.getSafeCounter(),
        asyncQuizService.getSynchronizedCounter()
    ));
  }

  @DeleteMapping("/race/reset")
  public ResponseEntity<String> resetCounters() {
    asyncQuizService.resetCounters();
    return ResponseEntity.ok("Counters reset");
  }

  record RaceConditionResult(int unsafe, int safe, int synced) {}
}