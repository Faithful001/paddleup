package com.king.paddleup.infrastructure.security;

import com.king.paddleup.domain.user.UserRepository;
import com.king.paddleup.shared.exception.UserUnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String identifier) {
        return userRepository
                .findByUsernameOrEmail(identifier, identifier)
                .map(CustomUserDetails::new)
                .orElseThrow(() ->
                        new UserUnauthorizedException("Invalid credentials"));
    }
}