package com.example.onlinetest.dto;

import jakarta.validation.constraints.NotBlank;

public record AnswerRequest(
    @NotBlank(message = "Answer text is required")
    String text,

    Boolean isCorrect
) { }