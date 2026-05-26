package com.example.onlinetest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на регистрацию")
public class RegisterRequest {

  @NotBlank(message = "Username is required")
  @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
  @Schema(description = "Имя пользователя", example = "newuser")
  private String username;

  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  @Schema(description = "Email", example = "user@example.com")
  private String email;

  @NotBlank(message = "Password is required")
  @Size(min = 3, max = 100, message = "Password must be between 6 and 100 characters")
  @Schema(description = "Пароль", example = "password123")
  private String password;

  @Schema(description = "Имя", example = "John")
  private String firstName;

  @Schema(description = "Фамилия", example = "Doe")
  private String lastName;
}