package com.infrastructure.monitoring.service;

import com.infrastructure.monitoring.dto.*;
import com.infrastructure.monitoring.entity.User;
import com.infrastructure.monitoring.exception.BadRequestException;
import com.infrastructure.monitoring.exception.ResourceNotFoundException;
import com.infrastructure.monitoring.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        if (username == null || password == null) {
            return null;
        }

        User user = userRepository.findByUsername(username.trim())
                .orElse(null);

        if (user == null) {
            return null;
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return null;
        }

        return jwtService.generateToken(
                user.getUsername(),
                user.getRole()
        );
    }

    @Transactional
    public User register(String username, String password, String role) {
        if (username == null || username.trim().isEmpty()) {
            throw new BadRequestException("Username cannot be empty");
        }
        if (password == null || password.length() < 6) {
            throw new BadRequestException("Password must be at least 6 characters long");
        }

        String sanitizedUsername = username.trim();
        if (userRepository.existsByUsername(sanitizedUsername)) {
            throw new BadRequestException("Username is already taken");
        }

        // Public signup can ONLY create a VIEWER account
        String assignedRole = "VIEWER";
        if ("VIEWER".equalsIgnoreCase(role)) {
            assignedRole = "VIEWER";
        }

        User user = new User();
        user.setUsername(sanitizedUsername);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(assignedRole);

        return userRepository.save(user);
    }

    public ForgotPasswordResponse generateForgotPasswordToken(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new BadRequestException("Username or email is required");
        }

        String sanitizedUsername = username.trim();
        User user = userRepository.findByUsername(sanitizedUsername)
                .orElse(null);

        if (user == null) {
            // Generic message for security, while also informing
            return new ForgotPasswordResponse(
                    "If an account exists for " + sanitizedUsername + ", a password reset link has been generated.",
                    null,
                    null
            );
        }

        String resetToken = jwtService.generateResetToken(user.getUsername());
        String resetUrl = "http://localhost:5173/reset-password?token=" + resetToken;

        return new ForgotPasswordResponse(
                "Password reset link generated successfully. Please use the link within 15 minutes to reset your password.",
                resetToken,
                resetUrl
        );
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        if (token == null || token.trim().isEmpty()) {
            throw new BadRequestException("Reset token is required");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new BadRequestException("Password must be at least 6 characters long");
        }

        boolean isValid = jwtService.isTokenValidForPurpose(token, "PASSWORD_RESET")
                || jwtService.isTokenValidForPurpose(token, "OFFICER_INVITE");

        if (!isValid) {
            throw new BadRequestException("Invalid or expired password reset link");
        }

        String username = jwtService.extractUsername(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for token"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void changePassword(String username, String currentPassword, String newPassword) {
        if (username == null || username.trim().isEmpty()) {
            throw new BadRequestException("User not authenticated");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new BadRequestException("New password must be at least 6 characters long");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BadRequestException("Incorrect current password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public List<UserSummaryDTO> getOfficers() {
        return userRepository.findAll().stream()
                .filter(u -> "OFFICER".equalsIgnoreCase(u.getRole()) || "ADMIN".equalsIgnoreCase(u.getRole()))
                .map(u -> new UserSummaryDTO(u.getId(), u.getUsername(), u.getRole()))
                .collect(Collectors.toList());
    }

    @Transactional
    public OfficerInviteResponse inviteOfficer(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new BadRequestException("Officer username or email is required");
        }

        String sanitizedUsername = username.trim();
        User existingUser = userRepository.findByUsername(sanitizedUsername).orElse(null);

        User officer;
        if (existingUser != null) {
            if ("OFFICER".equalsIgnoreCase(existingUser.getRole()) || "ADMIN".equalsIgnoreCase(existingUser.getRole())) {
                String inviteToken = jwtService.generateOfficerInviteToken(existingUser.getUsername());
                String setupUrl = "http://localhost:5173/setup-officer?token=" + inviteToken;
                return new OfficerInviteResponse(
                        existingUser.getUsername(),
                        inviteToken,
                        setupUrl,
                        "Officer account already exists. Secure login/setup link generated."
                );
            } else {
                // Elevate Viewer to Officer
                existingUser.setRole("OFFICER");
                officer = userRepository.save(existingUser);
            }
        } else {
            // Create user with temporary random placeholder password until officer sets it
            officer = new User();
            officer.setUsername(sanitizedUsername);
            officer.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            officer.setRole("OFFICER");
            officer = userRepository.save(officer);
        }

        String inviteToken = jwtService.generateOfficerInviteToken(officer.getUsername());
        String setupUrl = "http://localhost:5173/setup-officer?token=" + inviteToken;

        return new OfficerInviteResponse(
                officer.getUsername(),
                inviteToken,
                setupUrl,
                "Officer invitation created successfully. Share the secure setup link with the officer."
        );
    }

    @Transactional
    public void setupOfficer(String token, String password) {
        if (token == null || token.trim().isEmpty()) {
            throw new BadRequestException("Setup token is required");
        }
        if (password == null || password.length() < 6) {
            throw new BadRequestException("Password must be at least 6 characters long");
        }

        if (!jwtService.isTokenValidForPurpose(token, "OFFICER_INVITE") && !jwtService.isTokenValidForPurpose(token, "PASSWORD_RESET")) {
            throw new BadRequestException("Invalid or expired invitation/setup token");
        }

        String username = jwtService.extractUsername(token);
        User user = userRepository.findByUsername(username)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername(username);
                    newUser.setRole("OFFICER");
                    return newUser;
                });

        user.setRole("OFFICER");
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }
}