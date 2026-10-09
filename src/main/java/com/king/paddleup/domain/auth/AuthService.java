
package com.king.paddleup.domain.auth;

import com.king.paddleup.domain.auth.dto.AuthResponse;
import com.king.paddleup.domain.auth.dto.LoginWithEmailRequest;
import com.king.paddleup.domain.auth.dto.LoginWithUsernameRequest;
import com.king.paddleup.domain.auth.dto.RegisterRequest;
import com.king.paddleup.domain.user.User;
import com.king.paddleup.domain.user.UserRepository;
import com.king.paddleup.infrastructure.security.CustomUserDetails;
import com.king.paddleup.infrastructure.security.jwt.JwtService;
import com.king.paddleup.shared.exception.UserUnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .password(passwordEncoder.encode(request.password()))
                .isSuspended(false)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user.getId(), user.getEmail());

        return new AuthResponse(token, "Bearer");
    }

    public AuthResponse loginWithEmail(LoginWithEmailRequest request) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.email(),
                                request.password()
                        )
                );

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        if (userDetails == null) {
            throw new UserUnauthorizedException("Invalid username or password");
        }

        String token = jwtService.generateToken(
                userDetails.getId(),
                userDetails.getEmail()
        );

        return new AuthResponse(token, "Bearer");
    }

    public AuthResponse loginWithUsername(LoginWithUsernameRequest request) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.username(),
                                request.password()
                        )
                );

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        if (userDetails == null) {
            throw new UserUnauthorizedException("Invalid username or password");
        }

        String token = jwtService.generateToken(
                userDetails.getId(),
                userDetails.getEmail()
        );

        return new AuthResponse(token, "Bearer");
    }

    private AuthResponse createTokenForActiveUser(User user) {
        if (Boolean.TRUE.equals(user.getIsSuspended())) {
            throw new UserUnauthorizedException(
                    "Your account is suspended"
            );
        }

        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail()
        );

        return new AuthResponse(token, "Bearer");
    }
}