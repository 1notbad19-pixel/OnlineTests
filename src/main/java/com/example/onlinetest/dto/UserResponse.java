package com.example.onlinetest.dto;

import java.time.LocalDateTime;

public record UserResponse(
    Long id,
    String username,
    String email,
    String firstName,
    String lastName,
    LocalDateTime createdAt,
    Integer quizzesCount
) { }