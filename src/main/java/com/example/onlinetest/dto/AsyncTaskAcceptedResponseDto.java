package com.example.onlinetest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ при создании асинхронной задачи")
public class AsyncTaskAcceptedResponseDto {

    @Schema(description = "ID асинхронной задачи", example = "3e3f95e2-c530-4bcf-a534-17be32789f56")
  private String taskId;
}