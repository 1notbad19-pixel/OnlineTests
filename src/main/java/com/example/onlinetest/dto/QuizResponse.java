package com.example.onlinetest.dto;

import java.time.LocalDateTime;
import java.util.List;

public record QuizResponse(
        Long id,
        String title,
        String description,
        String category,
        Integer timeLimitMinutes,
        Integer maxAttempts,
        Boolean isPublished,
        Integer passingScore,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<String> tags,
        Integer questionCount
) {}