package com.example.onlinetest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Запрос на создание ответа")
public record AnswerRequest(
    @Schema(description = "Текст ответа", example = "Язык программирования")
    @NotBlank(message = "Answer text is required")
    String text,

    @Schema(description = "Правильный ли ответ?", example = "true")
    Boolean isCorrect
) { }