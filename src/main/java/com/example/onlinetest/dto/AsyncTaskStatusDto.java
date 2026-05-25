package com.example.onlinetest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Статус асинхронной задачи")
public class AsyncTaskStatusDto {

    @Schema(description = "ID асинхронной задачи", example = "3e3f95e2-c530-4bcf-a534-17be32789f56")
  private String taskId;

    @Schema(description = "Текущий статус", example = "RUNNING",
        allowableValues = {"PENDING", "RUNNING", "COMPLETED", "FAILED"})
  private String status;

    @Schema(description = "Время создания задачи", example = "2026-04-21T15:20:00")
  private LocalDateTime createdAt;

    @Schema(description = "Время начала выполнения", example = "2026-04-21T15:20:01")
  private LocalDateTime startedAt;

    @Schema(description = "Время завершения", example = "2026-04-21T15:20:05")
  private LocalDateTime completedAt;

    @Schema(description = "Количество обработанных элементов", example = "3")
  private Integer processedItems;

    @Schema(description = "Сообщение об ошибке", example = "Matches list cannot be empty")
  private String errorMessage;
}