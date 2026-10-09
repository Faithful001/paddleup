package com.king.paddleup.domain.auth;

import com.king.paddleup.domain.auth.dto.AuthResponse;
import com.king.paddleup.domain.auth.dto.LoginWithEmailRequest;
import com.king.paddleup.domain.auth.dto.LoginWithUsernameRequest;
import com.king.paddleup.domain.auth.dto.RegisterRequest;
import com.king.paddleup.shared.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Response<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        Response.success(
                        "Registration successful",
                        authService.register(request)
                )
        );
    }

    @PostMapping("/login/email")
    public ResponseEntity<Response<AuthResponse>> loginWithEmail(
            @Valid @RequestBody LoginWithEmailRequest request
    ) {
        return ResponseEntity.ok(
                Response.success(
                        "Log in successful",
                        authService.loginWithEmail(request)
                )
        );
    }

    @PostMapping("/login/username")
    public ResponseEntity<Response<AuthResponse>> loginWithUsername(
            @Valid @RequestBody LoginWithUsernameRequest request
    ) {
        return ResponseEntity.ok(
                Response.success(
                        "Log in successful",
                        authService.loginWithUsername(request)
                )
        );
    }
}