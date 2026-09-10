package com.infrastructure.monitoring.controller;

import com.infrastructure.monitoring.dto.*;
import com.infrastructure.monitoring.entity.User;
import com.infrastructure.monitoring.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(
                request.getUsername(),
                request.getPassword()
        );

        if (token == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Invalid username or password"));
        }

        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<UserSummaryDTO> register(@Valid @RequestBody LoginRequest request) {
        // Public registration strictly creates a VIEWER account
        User user = authService.register(
                request.getUsername(),
                request.getPassword(),
                "VIEWER"
        );

        return ResponseEntity.ok(new UserSummaryDTO(user.getId(), user.getUsername(), user.getRole()));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        ForgotPasswordResponse response = authService.generateForgotPasswordToken(request.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password has been successfully reset. You can now login with your new password."));
    }

    @PostMapping("/setup-officer")
    public ResponseEntity<Map<String, String>> setupOfficer(@Valid @RequestBody ResetPasswordRequest request) {
        authService.setupOfficer(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Officer account setup complete. You can now login."));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return ResponseEntity.status(401).body(Map.of("message", "User must be authenticated to change password"));
        }

        authService.changePassword(auth.getName(), request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password changed successfully."));
    }

    @GetMapping("/officers")
    public ResponseEntity<List<UserSummaryDTO>> getOfficers() {
        return ResponseEntity.ok(authService.getOfficers());
    }

    @PostMapping("/invite-officer")
    public ResponseEntity<OfficerInviteResponse> inviteOfficer(@Valid @RequestBody OfficerInviteRequest request) {
        OfficerInviteResponse response = authService.inviteOfficer(request.getUsername());
        return ResponseEntity.ok(response);
    }
}