package com.example.onlinetest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на вход")
public class AuthRequest {

  @NotBlank(message = "Username is required")
  @Schema(description = "Имя пользователя", example = "admin")
  private String username;

  @NotBlank(message = "Password is required")
  @Schema(description = "Пароль", example = "password")
  private String password;
}