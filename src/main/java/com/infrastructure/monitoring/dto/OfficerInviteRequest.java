package com.infrastructure.monitoring.dto;

import jakarta.validation.constraints.NotBlank;

public class OfficerInviteRequest {

    @NotBlank(message = "Officer username or email is required")
    private String username;

    public OfficerInviteRequest() {
    }

    public OfficerInviteRequest(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
