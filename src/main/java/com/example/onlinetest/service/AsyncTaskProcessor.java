package com.example.onlinetest.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
public class AsyncTaskProcessor {

    @Async
  public CompletableFuture<Void> runTask(long durationMs) {
        try {
            Thread.sleep(durationMs);
            return CompletableFuture.completedFuture(null);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return CompletableFuture.failedFuture(new IllegalStateException("Async task interrupted", ex));
        }
    }
}