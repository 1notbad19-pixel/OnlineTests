package com.example.onlinetest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AnswerRequest(
    @NotBlank(message = "Answer text is required")
    String text,

    Boolean isCorrect,

    @NotNull(message = "Question ID is required")
    Long questionId
) { }