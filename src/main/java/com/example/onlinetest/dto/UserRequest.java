package com.example.onlinetest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на создание/обновление пользователя")
public record UserRequest(

    @Schema(description = "Имя пользователя (уникальное)", example = "john_doe")
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    String username,

    @Schema(description = "Email пользователя", example = "john@example.com")
    @NotBlank(message = "Email is required")
    @Email
    String email,

    @Schema(description = "Пароль", example = "password123")
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100)
    String password,

    @Schema(description = "Имя", example = "John")
    String firstName,

    @Schema(description = "Фамилия", example = "Doe")
    String lastName
) { }