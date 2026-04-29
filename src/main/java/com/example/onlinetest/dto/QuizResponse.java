package com.example.onlinetest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Ответ с данными квиза")
public record QuizResponse(
    @Schema(description = "ID квиза", example = "1")
    Long id,

    @Schema(description = "Название", example = "Java Programming Quiz")
    String title,

    @Schema(description = "Описание", example = "Проверь свои знания Java")
    String description,

    @Schema(description = "Категория", example = "Programming")
    String category,

    @Schema(description = "Время на прохождение (минуты)", example = "30")
    Integer timeLimitMinutes,

    @Schema(description = "Максимум попыток", example = "3")
    Integer maxAttempts,

    @Schema(description = "Опубликован?", example = "true")
    Boolean isPublished,

    @Schema(description = "Проходной балл", example = "70")
    Integer passingScore,

    @Schema(description = "Дата создания", example = "2026-04-28T12:00:00")
    LocalDateTime createdAt,

    @Schema(description = "Дата обновления", example = "2026-04-28T12:00:00")
    LocalDateTime updatedAt,

    @Schema(description = "Теги", example = "[\"java\", \"programming\"]")
    List<String> tags,

    @Schema(description = "Количество вопросов", example = "5")
    Integer questionCount
) {

}