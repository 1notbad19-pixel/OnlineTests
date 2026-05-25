package com.example.onlinetest.service;

import com.example.onlinetest.dto.AsyncTaskAcceptedResponseDto;
import com.example.onlinetest.dto.AsyncTaskStatusDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncTaskService {

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_RUNNING = "RUNNING";
    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String STATUS_FAILED = "FAILED";

    private final AsyncTaskProcessor asyncTaskProcessor;
    private final Map<String, TaskState> tasks = new ConcurrentHashMap<>();

    public AsyncTaskAcceptedResponseDto startTask(long durationMs) {
        if (durationMs <= 0) {
            throw new IllegalArgumentException("durationMs must be > 0");
        }

        String taskId = UUID.randomUUID().toString();
        TaskState taskState = new TaskState();
        tasks.put(taskId, taskState);

        try {
            CompletableFuture<Void> taskFuture = asyncTaskProcessor.runTask(durationMs);
            taskState.markRunning();

            taskFuture.whenComplete((result, error) -> {
                if (error != null) {
                    taskState.markFailed(extractErrorMessage(error));
                    log.error("Задача {} завершилась с ошибкой: {}", taskId, error.getMessage());
                    return;
                }
                taskState.markCompleted();
                log.info("Задача {} успешно завершена", taskId);
            });
        } catch (RejectedExecutionException ex) {
            tasks.remove(taskId);
            throw ex;
        }

        return new AsyncTaskAcceptedResponseDto(taskId);
    }

    public AsyncTaskStatusDto getTaskStatus(String taskId) {
        TaskState taskState = Optional.ofNullable(tasks.get(taskId))
            .orElseThrow(() -> new NoSuchElementException("Task not found: " + taskId));
        return taskState.toStatusDto(taskId);
    }

    private String extractErrorMessage(Throwable throwable) {
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }

        return Optional.ofNullable(rootCause.getMessage())
        .filter(message -> !message.isBlank())
        .orElse(rootCause.getClass().getSimpleName());
    }

    private static final class TaskState {

        private final LocalDateTime createdAt = LocalDateTime.now();
        private String status = STATUS_PENDING;
        private LocalDateTime startedAt;
        private LocalDateTime completedAt;
        private Integer processedItems;
        private String errorMessage;

        private synchronized void markRunning() {
            status = STATUS_RUNNING;
            startedAt = LocalDateTime.now();
            errorMessage = null;
        }

        private synchronized void markCompleted() {
            status = STATUS_COMPLETED;
            completedAt = LocalDateTime.now();
            processedItems = 1;
            errorMessage = null;
        }

        private synchronized void markFailed(String message) {
            status = STATUS_FAILED;
            completedAt = LocalDateTime.now();
            errorMessage = message;
        }

        private synchronized AsyncTaskStatusDto toStatusDto(String taskId) {
            return new AsyncTaskStatusDto(
                taskId,
                status,
                createdAt,
                startedAt,
                completedAt,
                processedItems,
                errorMessage
      );
        }
    }
}