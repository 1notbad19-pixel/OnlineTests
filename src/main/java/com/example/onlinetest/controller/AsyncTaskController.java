package com.example.onlinetest.controller;

import com.example.onlinetest.dto.AsyncTaskAcceptedResponseDto;
import com.example.onlinetest.dto.AsyncTaskStatusDto;
import com.example.onlinetest.service.AsyncTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/async-task")
@RequiredArgsConstructor
@Tag(name = "Async Operations", description = "Асинхронные операции")
public class AsyncTaskController {

    private final AsyncTaskService asyncTaskService;

    @PostMapping("/start")
    @Operation(
        summary = "Запустить асинхронную задачу",
        description = "Запускает асинхронную задачу и возвращает ID задачи"
    )
  public ResponseEntity<AsyncTaskAcceptedResponseDto> startTask(
        @Parameter(description = "Длительность задачи в миллисекундах", example = "5000")
        @RequestParam(defaultValue = "5000") long durationMs
    ) {
        return ResponseEntity.accepted().body(asyncTaskService.startTask(durationMs));
    }

    @GetMapping("/tasks/{taskId}")
  @Operation(
      summary = "Получить статус асинхронной задачи",
      description = "Возвращает статус выполнения задачи по ID"
  )
  public ResponseEntity<AsyncTaskStatusDto> getTaskStatus(
        @Parameter(description = "ID задачи", example = "3e3f95e2-c530-4bcf-a534-17be32789f56")
        @PathVariable String taskId
    ) {
        return ResponseEntity.ok(asyncTaskService.getTaskStatus(taskId));
    }
}