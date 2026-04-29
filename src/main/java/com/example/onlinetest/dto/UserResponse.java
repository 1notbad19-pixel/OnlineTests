package com.example.onlinetest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Ответ с данными пользователя")
public record UserResponse(

    @Schema(description = "ID пользователя", example = "1")
    Long id,

    @Schema(description = "Имя пользователя", example = "john_doe")
    String username,

    @Schema(description = "Email пользователя", example = "john@example.com")
    String email,

    @Schema(description = "Имя", example = "John")
    String firstName,

    @Schema(description = "Фамилия", example = "Doe")
    String lastName,

    @Schema(description = "Дата регистрации", example = "2026-04-28T12:00:00")
    LocalDateTime createdAt,

    @Schema(description = "Количество созданных квизов", example = "5")
    Integer quizzesCount
) { }