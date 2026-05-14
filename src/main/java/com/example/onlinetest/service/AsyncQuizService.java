package com.example.onlinetest.service;

import com.example.onlinetest.dto.QuizRequest;
import com.example.onlinetest.dto.QuizResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncQuizService {

  private final QuizService quizService;

  // Хранилище статусов задач
  private final ConcurrentHashMap<String, String> taskStatus = new ConcurrentHashMap<>();

  // Потокобезопасный счётчик для ID задач
  private final AtomicInteger taskCounter = new AtomicInteger(0);

  // ==================== НЕБЕЗОПАСНЫЙ СЧЁТЧИК (для демонстрации race condition) ====================
  private int unsafeCounter = 0;

  // ==================== БЕЗОПАСНЫЕ СЧЁТЧИКИ ====================
  private final AtomicInteger safeCounter = new AtomicInteger(0);
  private int synchronizedCounter = 0;

  // ==================== АСИНХРОННАЯ ОПЕРАЦИЯ ====================

  @Async
  public CompletableFuture<String> createQuizAsync(QuizRequest request) {
    String taskId = "TASK-" + System.currentTimeMillis() + "-" + taskCounter.incrementAndGet();
    taskStatus.put(taskId, "IN_PROGRESS");

    try {
      log.info("Асинхронное создание квиза, задача: {}", taskId);

      // Имитация длительной операции (3 секунды)
      Thread.sleep(3000);

      QuizResponse response = quizService.createQuiz(request);

      taskStatus.put(taskId, "COMPLETED");
      log.info("Асинхронный квиз создан: {}", response.id());
      return CompletableFuture.completedFuture(taskId);
    } catch (Exception e) {
      taskStatus.put(taskId, "FAILED: " + e.getMessage());
      log.error("Ошибка при создании квиза: {}", e.getMessage());
      return CompletableFuture.failedFuture(e);
    }
  }

  public String getTaskStatus(String taskId) {
    return taskStatus.getOrDefault(taskId, "NOT_FOUND");
  }

  // ==================== НЕБЕЗОПАСНЫЙ ИНКРЕМЕНТ (race condition) ====================

  public void incrementUnsafe() {
    unsafeCounter++;
  }

  public int getUnsafeCounter() {
    return unsafeCounter;
  }

  // ==================== БЕЗОПАСНЫЙ ИНКРЕМЕНТ (Atomic) ====================

  public void incrementSafe() {
    safeCounter.incrementAndGet();
  }

  public int getSafeCounter() {
    return safeCounter.get();
  }

  // ==================== БЕЗОПАСНЫЙ ИНКРЕМЕНТ (synchronized) ====================

  public synchronized void incrementSynchronized() {
    synchronizedCounter++;
  }

  public int getSynchronizedCounter() {
    return synchronizedCounter;
  }

  // ==================== СБРОС СЧЁТЧИКОВ ====================

  public void resetCounters() {
    unsafeCounter = 0;
    safeCounter.set(0);
    synchronizedCounter = 0;
  }
}