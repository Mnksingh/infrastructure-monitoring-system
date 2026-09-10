package com.infrastructure.monitoring.dto;

import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordRequest {

    @NotBlank(message = "Username or email is required")
    private String username;

    public ForgotPasswordRequest() {
    }

    public ForgotPasswordRequest(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
