package com.king.paddleup.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
        @Pattern(
                regexp = "^[a-zA-Z0-9_]+$",
                message = "Username can only contain letters, numbers, and underscores"
        )
        String username,

        @NotBlank(message = "Email is required")
        @Size(max = 50, message = "Email must not be longer than 50 characters")
        @Email(message = "Invalid email address")
        String email,

        @NotBlank(message = "First name is required")
        @Size(max = 20, message = "First name must not be longer than 20 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 20, message = "Last name must not be longer than 20 characters")
        String lastName,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password
) {}