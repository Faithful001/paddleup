package com.king.paddleup.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginWithEmailRequest(
        @NotBlank(message = "email is required")
        @Size(max = 50, message = "email should not be longer than 80 characters")
        @Email
        String email,

        @NotBlank(message = "password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password
) {}
