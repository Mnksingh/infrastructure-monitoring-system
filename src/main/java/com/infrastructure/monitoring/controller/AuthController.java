package com.infrastructure.monitoring.controller;

import com.infrastructure.monitoring.dto.LoginRequest;
import com.infrastructure.monitoring.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.infrastructure.monitoring.entity.User;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody LoginRequest request) {

        String token = authService.login(
                request.getUsername(),
                request.getPassword()
        );

        if (token == null) {
            return ResponseEntity.status(401)
                    .body("Invalid username or password");
        }

        return ResponseEntity.ok(token);
    }
    @PostMapping("/register")
    public ResponseEntity<User> register(
            @RequestBody LoginRequest request) {

        User user = authService.register(
                request.getUsername(),
                request.getPassword(),
                "OFFICER"
        );

        return ResponseEntity.ok(user);
    }
}