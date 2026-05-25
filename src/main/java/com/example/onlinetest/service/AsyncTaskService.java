package com.example.onlinetest.service;

import com.example.onlinetest.dto.AsyncTaskAcceptedResponseDto;
import com.example.onlinetest.dto.AsyncTaskStatusDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;
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
        if (durationMs <= 0) {
            throw new IllegalArgumentException("durationMs must be greater than 0");
        }

        String taskId = UUID.randomUUID().toString();

        TaskInfo taskInfo = new TaskInfo(taskId, "PENDING", LocalDateTime.now());
        tasks.put(taskId, taskInfo);

        CompletableFuture<String> future = asyncTaskProcessor.runTask(durationMs);

        taskInfo.setStatus("RUNNING");
        taskInfo.setStartedAt(LocalDateTime.now());

        future.whenComplete((result, error) -> {
            if (error != null) {
                taskInfo.setStatus("FAILED");
                taskInfo.setErrorMessage(error.getMessage());
                taskInfo.setCompletedAt(LocalDateTime.now());
                log.error("Задача {} завершилась ошибкой: {}", taskId, error.getMessage());
            } else {
                taskInfo.setStatus("COMPLETED");
                taskInfo.setCompletedAt(LocalDateTime.now());
                taskInfo.setProcessedItems(1);
                log.info("Задача {} успешно завершена", taskId);
            }
        });

        return new AsyncTaskAcceptedResponseDto(taskId);
    }

    public AsyncTaskStatusDto getTaskStatus(String taskId) {
        TaskInfo taskInfo = tasks.get(taskId);
        if (taskInfo == null) {
            throw new NoSuchElementException("Task not found: " + taskId);
        }

        return new AsyncTaskStatusDto(
            taskInfo.getTaskId(),
            taskInfo.getStatus(),
            taskInfo.getCreatedAt(),
            taskInfo.getStartedAt(),
            taskInfo.getCompletedAt(),
            taskInfo.getProcessedItems(),
            taskInfo.getErrorMessage()
        );
    }

    @Getter
    @Setter
    private static class TaskInfo {
        private final String taskId;
        private final LocalDateTime createdAt;
        private String status;
        private LocalDateTime startedAt;
        private LocalDateTime completedAt;
        private Integer processedItems;
        private String errorMessage;

        public TaskInfo(String taskId, String status, LocalDateTime createdAt) {
            this.taskId = taskId;
            this.status = status;
            this.createdAt = createdAt;
        }
    }
}