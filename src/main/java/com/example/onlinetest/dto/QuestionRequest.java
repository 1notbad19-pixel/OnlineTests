package com.example.onlinetest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record QuestionRequest(
    @NotBlank(message = "Question text is required")
    String text,

    @NotBlank(message = "Question type is required")
    String type,

    @NotNull(message = "Points are required")
    @Min(1)
    Integer points,

    List<AnswerRequest> answers
) { }