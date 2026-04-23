package com.example.onlinetest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    String username,

    @NotBlank(message = "Email is required")
    @Email
    String email,

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100)
    String password,

    String firstName,
    String lastName
) { }