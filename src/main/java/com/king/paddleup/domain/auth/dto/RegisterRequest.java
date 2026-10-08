package com.king.paddleup.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "username is required")
        @Size(max = 80, message = "username should not be longer than 80 characters")
        String username,

        @NotBlank(message = "email is required")
        @Size(max = 80, message = "email should not be longer than 80 characters")
        @Email
        String email,

        @NotBlank(message = "firstName is required")
        @Size(max = 20, message = "firstName should not be longer than 80 characters")
        String firstName,

        @NotBlank(message = "firstName is required")
        @Size(max = 20, message = "firstName should not be longer than 80 characters")
        String lastName,

        @NotBlank(message = "password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password
) {}
