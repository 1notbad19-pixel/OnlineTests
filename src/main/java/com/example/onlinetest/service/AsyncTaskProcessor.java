package com.example.onlinetest.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
public class AsyncTaskProcessor {

  @Async
  public CompletableFuture<String> runTask(long durationMs) {
    try {
      Thread.sleep(durationMs);
      return CompletableFuture.completedFuture("SUCCESS");
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      return CompletableFuture.failedFuture(new RuntimeException("Task was interrupted", ex));
    }
  }
}