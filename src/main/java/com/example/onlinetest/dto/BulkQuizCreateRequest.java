package com.example.onlinetest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record BulkQuizCreateRequest(
    @NotEmpty(message = "Quiz list cannot be empty")
    List<@Valid QuizRequest> quizzes
) { }