package com.example.onlinetest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record FullQuizRequest(
    @Valid
    @NotNull(message = "Quiz data is required")
    QuizRequest quiz,

    List<@Valid QuestionRequest> questions
) { }