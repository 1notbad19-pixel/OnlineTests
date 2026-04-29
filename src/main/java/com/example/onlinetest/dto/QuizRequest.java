package com.example.onlinetest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Запрос на создание/обновление квиза")
public record QuizRequest(
    @Schema(description = "Название квиза", example = "Java Programming Quiz")
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200)
    String title,

    @Schema(description = "Описание квиза", example = "Проверь свои знания Java")
    @Size(max = 1000)
    String description,

    @Schema(description = "Категория", example = "Programming")
    @NotBlank
    String category,

    @Schema(description = "Время на прохождение (минуты)", example = "30")
    @NotNull
    @Min(1)
    Integer timeLimitMinutes,

    @Schema(description = "Максимальное количество попыток", example = "3")
    @Min(1)
    Integer maxAttempts,

    @Schema(description = "Опубликован?", example = "true")
    Boolean isPublished,

    @Schema(description = "Проходной балл", example = "70")
    @Min(0)
    @Max(100)
    Integer passingScore,

    @Schema(description = "Теги", example = "[\"java\", \"programming\"]")
    List<String> tags
) {

}