package com.example.onlinetest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ при успешной авторизации")
public class AuthResponse {

  @Schema(description = "JWT токен (временно - просто строка)", example = "dummy-token-12345")
  private String token;

  @Schema(description = "Имя пользователя", example = "admin")
  private String username;

  @Schema(description = "Email", example = "admin@example.com")
  private String email;

  @Schema(description = "Роль", example = "USER")
  private String role;
}