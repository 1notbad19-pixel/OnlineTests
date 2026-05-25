package com.example.onlinetest.service;

import com.example.onlinetest.dto.AsyncTaskAcceptedResponseDto;
import com.example.onlinetest.dto.AsyncTaskStatusDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncTaskService {

    private final AsyncTaskProcessor asyncTaskProcessor;
    private final Map<String, TaskInfo> tasks = new ConcurrentHashMap<>();

    public AsyncTaskAcceptedResponseDto startTask(long durationMs) {
        String taskId = UUID.randomUUID().toString();

        TaskInfo taskInfo = new TaskInfo(taskId, "PENDING", LocalDateTime.now());
        tasks.put(taskId, taskInfo);

        // ← Этот код правильный
        CompletableFuture<String> future = asyncTaskProcessor.runTask(durationMs);

        future.whenComplete((result, error) -> {
            if (error != null) {
                tasks.put(taskId, new TaskInfo(taskId, "FAILED", LocalDateTime.now(), error.getMessage()));
                log.error("Задача {} завершилась ошибкой", taskId);
            } else {
                tasks.put(taskId, new TaskInfo(taskId, "COMPLETED", LocalDateTime.now()));
                log.info("Задача {} успешно завершена", taskId);
            }
        });

        return new AsyncTaskAcceptedResponseDto(taskId);
    }

    public AsyncTaskStatusDto getTaskStatus(String taskId) {
        TaskInfo taskInfo = tasks.get(taskId);
        if (taskInfo == null) {
            throw new RuntimeException("Task not found: " + taskId);
        }

        return new AsyncTaskStatusDto(
            taskInfo.getTaskId(),
            taskInfo.getStatus(),
            taskInfo.getCreatedAt(),
            null,
            taskInfo.getCompletedAt(),
            null,
            taskInfo.getErrorMessage()
        );
    }

    private static class TaskInfo {
        private final String taskId;
        private final String status;
        private final LocalDateTime createdAt;
        private final LocalDateTime completedAt;
        private final String errorMessage;

        public TaskInfo(String taskId, String status, LocalDateTime timestamp) {
            this(taskId, status, timestamp, null);
        }

        public TaskInfo(String taskId, String status, LocalDateTime timestamp, String errorMessage) {
            this.taskId = taskId;
            this.status = status;
            this.createdAt = timestamp;
            this.completedAt = "COMPLETED".equals(status) || "FAILED".equals(status) ? timestamp : null;
            this.errorMessage = errorMessage;
        }

        public String getTaskId() { return taskId; }
        public String getStatus() { return status; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public LocalDateTime getCompletedAt() { return completedAt; }
        public String getErrorMessage() { return errorMessage; }
    }
}