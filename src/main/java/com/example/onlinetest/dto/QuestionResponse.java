package com.example.onlinetest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Ответ с данными вопроса")
public record QuestionResponse(
    @Schema(description = "ID вопроса", example = "1")
    Long id,

    @Schema(description = "Текст вопроса", example = "Что такое Java?")
    String text,

    @Schema(description = "Тип вопроса", example = "SINGLE")
    String type,

    @Schema(description = "Баллы за вопрос", example = "10")
    Integer points,

    @Schema(description = "Варианты ответов")
    List<AnswerResponse> answers
) { }