package com.example.onlinetest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "Запрос на создание вопроса")
public record QuestionRequest(
    @Schema(description = "Текст вопроса", example = "Что такое Java?")
    @NotBlank(message = "Question text is required")
    String text,

    @Schema(description = "Тип вопроса", example = "SINGLE")
    @NotBlank(message = "Question type is required")
    String type,

    @Schema(description = "Баллы за вопрос", example = "10")
    @NotNull(message = "Points are required")
    @Min(1)
    Integer points,

    @Schema(description = "Варианты ответов")
    List<AnswerRequest> answers
) { }