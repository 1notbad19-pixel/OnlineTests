package com.example.onlinetest.controller;

import com.example.onlinetest.dto.AuthRequest;
import com.example.onlinetest.dto.AuthResponse;
import com.example.onlinetest.dto.RegisterRequest;
import com.example.onlinetest.model.User;
import com.example.onlinetest.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Авторизация и регистрация")
public class AuthController {

  private final UserRepository userRepository;

  @PostMapping("/login")
  @Operation(summary = "Вход в систему")
  public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
    Optional<User> userOpt = userRepository.findByUsername(request.getUsername());

    if (userOpt.isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("error", "Invalid username or password"));
    }

    User user = userOpt.get();

    // Временная проверка пароля (без хеширования)
    if (!user.getPassword().equals(request.getPassword())) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("error", "Invalid username or password"));
    }

    AuthResponse response = new AuthResponse(
        "dummy-token-" + System.currentTimeMillis(),
        user.getUsername(),
        user.getEmail(),
        "USER"
    );

    return ResponseEntity.ok(response);
  }

  @PostMapping("/register")
  @Operation(summary = "Регистрация нового пользователя")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
    // Проверка уникальности username
    if (userRepository.existsByUsername(request.getUsername())) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body(Map.of("error", "Username already exists"));
    }

    // Проверка уникальности email
    if (userRepository.existsByEmail(request.getEmail())) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body(Map.of("error", "Email already exists"));
    }

    // Создание нового пользователя
    User newUser = new User();
    newUser.setUsername(request.getUsername());
    newUser.setEmail(request.getEmail());
    newUser.setPassword(request.getPassword()); // Временно без хеширования
    newUser.setFirstName(request.getFirstName());
    newUser.setLastName(request.getLastName());
    newUser.setCreatedAt(LocalDateTime.now());

    User savedUser = userRepository.save(newUser);
    log.info("Новый пользователь зарегистрирован: {}", savedUser.getUsername());

    AuthResponse response = new AuthResponse(
        "dummy-token-" + System.currentTimeMillis(),
        savedUser.getUsername(),
        savedUser.getEmail(),
        "USER"
    );

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/me")
  @Operation(summary = "Получить информацию о текущем пользователе")
  public ResponseEntity<?> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String authHeader) {
    // Временная реализация без JWT
    // В реальном проекте нужно парсить токен и извлекать пользователя

    Map<String, Object> response = new HashMap<>();
    response.put("authenticated", authHeader != null && authHeader.startsWith("Bearer"));
    response.put("message", "JWT implementation would go here");

    return ResponseEntity.ok(response);
  }
}