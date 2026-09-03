package com.infrastructure.monitoring.service;

import com.infrastructure.monitoring.entity.User;
import com.infrastructure.monitoring.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String login(String username, String password) {

        User user = userRepository.findByUsername(username)
                .orElse(null);

        if (user == null) {
            return null;
        }

        if (!passwordEncoder.matches(
                password,
                user.getPassword())) {

            return null;
        }

        return jwtService.generateToken(
                user.getUsername(),
                user.getRole()
        );
    }

    public User register(
            String username,
            String password,
            String role) {

        User user = new User();

        user.setUsername(username);
        user.setPassword(
                passwordEncoder.encode(password)
        );
        user.setRole(role);

        return userRepository.save(user);
    }
}