package com.example.onlinetest.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public record QuizRequest(
        @NotBlank(message = "Title is required")
        @Size(min = 3, max = 200)
        String title,

        @Size(max = 1000)
        String description,

        @NotBlank
        String category,

        @NotNull @Min(1)
        Integer timeLimitMinutes,

        @Min(1)
        Integer maxAttempts,

        Boolean isPublished,

        @Min(0) @Max(100)
        Integer passingScore,

        List<String> tags
) {}