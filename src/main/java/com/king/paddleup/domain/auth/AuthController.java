package com.king.paddleup.domain.auth;

import com.king.paddleup.domain.auth.dto.*;
import com.king.paddleup.shared.exception.UserUnauthorizedException;
import com.king.paddleup.shared.response.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

    @PostMapping("/login")
    public ResponseEntity<Response<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(
                Response.success(
                        "Log in successful",
                        authService.login(request)
                )
        );
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Response<AuthResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(
                Response.success(
                        "Token refreshed successfully",
                        authService.refreshToken(request)
                )
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<Response<AuthResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(
                Response.success(
                        "Token refreshed successfully",
                        authService.refreshToken(request)
                )
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Response<Void>> logout(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader
    ) {
        authService.logout(authHeader);
        return ResponseEntity.ok(Response.message("Logged out successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<Response<UserProfileResponse>> getCurrentUser(
            @AuthenticationPrincipal UUID userId
    ) {
        if (userId == null) {
            throw new UserUnauthorizedException("User is not authenticated");
        }
        return ResponseEntity.ok(
                Response.success(
                        "User profile retrieved successfully",
                        authService.getCurrentUser(userId)
                )
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Response<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(
                Response.message("Password reset instructions sent to your email")
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Response<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        authService.resetPassword(request);
        return ResponseEntity.ok(
                Response.message("Password has been reset successfully")
        );
    }

    @PostMapping("/change-password")
    public ResponseEntity<Response<Void>> changePassword(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        if (userId == null) {
            throw new UserUnauthorizedException("User is not authenticated");
        }
        authService.changePassword(userId, request);
        return ResponseEntity.ok(
                Response.message("Password changed successfully")
        );
    }

    @PostMapping("/verify-email")
    public ResponseEntity<Response<Void>> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request
    ) {
        authService.verifyEmail(request.token());
        return ResponseEntity.ok(
                Response.message("Email verified successfully")
        );
    }

    @GetMapping("/verify-email")
    public ResponseEntity<Response<Void>> verifyEmailViaGet(
            @RequestParam("token") String token
    ) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(
                Response.message("Email verified successfully")
        );
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Response<Void>> resendVerification(
            @Valid @RequestBody ResendVerificationRequest request
    ) {
        authService.resendVerificationEmail(request);
        return ResponseEntity.ok(
                Response.message("Verification email resent successfully")
        );
    }
}