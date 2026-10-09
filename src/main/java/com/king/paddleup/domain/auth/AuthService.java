package com.king.paddleup.domain.auth;

import com.king.paddleup.domain.auth.dto.*;
import com.king.paddleup.domain.token.TokenService;
import com.king.paddleup.domain.token.TokenType;
import com.king.paddleup.domain.user.User;
import com.king.paddleup.domain.user.UserRepository;
import com.king.paddleup.infrastructure.email.EmailSender;
import com.king.paddleup.infrastructure.security.CustomUserDetails;
import com.king.paddleup.infrastructure.security.jwt.JwtProperties;
import com.king.paddleup.infrastructure.security.jwt.JwtService;
import com.king.paddleup.shared.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final TokenService tokenService;
    private final EmailSender emailSender;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException("Username is already taken");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("Email is already registered");
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .password(passwordEncoder.encode(request.password()))
                .isEmailVerified(false)
                .isSuspended(false)
                .build();

        user = userRepository.save(user);

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        Instant accessExpiry = Instant.now().plusMillis(jwtProperties.getAccessTokenExpirationMs());
        Instant refreshExpiry = Instant.now().plusMillis(jwtProperties.getRefreshTokenExpirationMs());

        tokenService.saveToken(user, accessToken, TokenType.ACCESS, accessExpiry);
        tokenService.saveToken(user, refreshToken, TokenType.REFRESH, refreshExpiry);

        // Generate verification token and try sending verification email
        try {
            String verificationToken = jwtService.generateEmailVerificationToken(user.getId(), user.getEmail());
            Instant verifyExpiry = Instant.now().plusMillis(jwtProperties.getEmailVerificationTokenExpirationMs());
            tokenService.saveToken(user, verificationToken, TokenType.EMAIL_VERIFICATION, verifyExpiry);
            sendVerificationEmail(user.getEmail(), verificationToken);
        } catch (Exception e) {
            log.warn("Failed to send verification email upon registration: {}", e.getMessage());
        }

        return new AuthResponse(
                accessToken,
                refreshToken,
                jwtProperties.getAccessTokenExpirationMs() / 1000,
                toUserDto(user)
        );
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.identifier(),
                        request.password()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        if (userDetails == null) {
            throw new UserUnauthorizedException("Invalid username or password");
        }

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (Boolean.TRUE.equals(user.getIsSuspended())) {
            throw new UserIsSuspendedException("Your account is suspended");
        }

        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        Instant accessExpiry = Instant.now().plusMillis(jwtProperties.getAccessTokenExpirationMs());
        Instant refreshExpiry = Instant.now().plusMillis(jwtProperties.getRefreshTokenExpirationMs());

        tokenService.saveToken(user, accessToken, TokenType.ACCESS, accessExpiry);
        tokenService.saveToken(user, refreshToken, TokenType.REFRESH, refreshExpiry);

        return new AuthResponse(
                accessToken,
                refreshToken,
                jwtProperties.getAccessTokenExpirationMs() / 1000,
                toUserDto(user)
        );
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();

        if (!jwtService.isTokenValid(refreshToken) || !tokenService.isTokenValid(refreshToken, TokenType.REFRESH)) {
            throw new InvalidTokenException("Invalid or expired refresh token");
        }

        UUID userId = jwtService.extractUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (Boolean.TRUE.equals(user.getIsSuspended())) {
            throw new UserIsSuspendedException("Your account is suspended");
        }

        // Rotate tokens: revoke the old refresh token
        tokenService.revokeToken(refreshToken);

        String newAccessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());
        String newRefreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        Instant accessExpiry = Instant.now().plusMillis(jwtProperties.getAccessTokenExpirationMs());
        Instant refreshExpiry = Instant.now().plusMillis(jwtProperties.getRefreshTokenExpirationMs());

        tokenService.saveToken(user, newAccessToken, TokenType.ACCESS, accessExpiry);
        tokenService.saveToken(user, newRefreshToken, TokenType.REFRESH, refreshExpiry);

        return new AuthResponse(
                newAccessToken,
                newRefreshToken,
                jwtProperties.getAccessTokenExpirationMs() / 1000,
                toUserDto(user)
        );
    }

    @Transactional
    public void logout(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring("Bearer ".length());
            tokenService.revokeToken(token);
        }
    }

    public UserProfileResponse getCurrentUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getIsEmailVerified(),
                user.getEmailVerifiedAt(),
                user.getIsSuspended(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException("User with email " + request.email() + " not found"));

        // Invalidate previous password reset tokens
        tokenService.revokeAllUserTokensByType(user.getId(), TokenType.PASSWORD_RESET);

        String resetToken = jwtService.generatePasswordResetToken(user.getId(), user.getEmail());
        Instant expiresAt = Instant.now().plusMillis(jwtProperties.getPasswordResetTokenExpirationMs());
        tokenService.saveToken(user, resetToken, TokenType.PASSWORD_RESET, expiresAt);

        try {
            sendPasswordResetEmail(user.getEmail(), resetToken);
        } catch (Exception e) {
            log.error("Failed to send password reset email: {}", e.getMessage());
            throw new EmailDeliveryException("Failed to send password reset email");
        }
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        if (!jwtService.isTokenValid(request.token()) || !tokenService.isTokenValid(request.token(), TokenType.PASSWORD_RESET)) {
            throw new InvalidTokenException("Invalid or expired password reset token");
        }

        UUID userId = jwtService.extractUserId(request.token());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        // Revoke the reset token and all active session tokens
        tokenService.revokeToken(request.token());
        tokenService.revokeAllUserTokens(userId);
    }

    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new UserUnauthorizedException("Current password does not match");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        // Revoke active sessions so user must log in again with new password
        tokenService.revokeAllUserTokens(userId);
    }

    @Transactional
    public void verifyEmail(String token) {
        if (!jwtService.isTokenValid(token) || !tokenService.isTokenValid(token, TokenType.EMAIL_VERIFICATION)) {
            throw new InvalidTokenException("Invalid or expired email verification token");
        }

        UUID userId = jwtService.extractUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setIsEmailVerified(true);
        user.setEmailVerifiedAt(Instant.now());
        userRepository.save(user);

        tokenService.revokeToken(token);
    }

    @Transactional
    public void resendVerificationEmail(ResendVerificationRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException("User with email " + request.email() + " not found"));

        if (Boolean.TRUE.equals(user.getIsEmailVerified())) {
            throw new DomainException("Email is already verified");
        }

        tokenService.revokeAllUserTokensByType(user.getId(), TokenType.EMAIL_VERIFICATION);

        String verificationToken = jwtService.generateEmailVerificationToken(user.getId(), user.getEmail());
        Instant verifyExpiry = Instant.now().plusMillis(jwtProperties.getEmailVerificationTokenExpirationMs());
        tokenService.saveToken(user, verificationToken, TokenType.EMAIL_VERIFICATION, verifyExpiry);

        try {
            sendVerificationEmail(user.getEmail(), verificationToken);
        } catch (Exception e) {
            log.error("Failed to resend verification email: {}", e.getMessage());
            throw new EmailDeliveryException("Failed to resend verification email");
        }
    }

    private void sendVerificationEmail(String email, String token) {
        String subject = "Verify your PaddleUp account";
        String html = "<p>Welcome to PaddleUp!</p>" +
                "<p>Please verify your email address using the following verification token:</p>" +
                "<p><strong>" + token + "</strong></p>";
        emailSender.send(email, subject, html);
    }

    private void sendPasswordResetEmail(String email, String token) {
        String subject = "PaddleUp Password Reset Request";
        String html = "<p>You requested a password reset for your PaddleUp account.</p>" +
                "<p>Please use the following token to reset your password:</p>" +
                "<p><strong>" + token + "</strong></p>" +
                "<p>This token will expire in 1 hour.</p>";
        emailSender.send(email, subject, html);
    }

    private AuthResponse.UserDto toUserDto(User user) {
        return new AuthResponse.UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getIsEmailVerified()
        );
    }
}