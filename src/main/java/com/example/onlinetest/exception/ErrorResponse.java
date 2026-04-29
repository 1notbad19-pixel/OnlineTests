package com.example.onlinetest.exception;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Стандартный формат ошибки")
public record ErrorResponse(
    @Schema(description = "Время ошибки", example = "2026-04-28T12:00:00")
    LocalDateTime timestamp,
    @Schema(description = "HTTP статус код", example = "400")
    int status,
    @Schema(description = "Название ошибки", example = "Bad Request")
    String error,
    @Schema(description = "Детальное сообщение", example = "Title is required")
    String message,
    @Schema(description = "Путь запроса", example = "/api/quizzes")
    String path

) { }