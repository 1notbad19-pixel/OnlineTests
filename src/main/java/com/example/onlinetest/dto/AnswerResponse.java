package com.example.onlinetest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с данными ответа")
public record AnswerResponse(
    @Schema(description = "ID ответа", example = "1")
    Long id,

    @Schema(description = "Текст ответа", example = "Язык программирования")
    String text,

    @Schema(description = "Правильный ли ответ?", example = "true")
    Boolean isCorrect
) { }