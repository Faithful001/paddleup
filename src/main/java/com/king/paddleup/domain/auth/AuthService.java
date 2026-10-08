package com.king.paddleup.domain.auth;

import com.king.paddleup.domain.user.UserRepository;
import com.king.paddleup.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserService userService;

    public void register() {

    }
}
