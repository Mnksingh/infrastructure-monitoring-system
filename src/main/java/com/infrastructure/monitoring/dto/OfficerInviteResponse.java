package com.infrastructure.monitoring.dto;

public class OfficerInviteResponse {

    private String username;
    private String inviteToken;
    private String setupUrl;
    private String message;

    public OfficerInviteResponse() {
    }

    public OfficerInviteResponse(String username, String inviteToken, String setupUrl, String message) {
        this.username = username;
        this.inviteToken = inviteToken;
        this.setupUrl = setupUrl;
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getInviteToken() {
        return inviteToken;
    }

    public void setInviteToken(String inviteToken) {
        this.inviteToken = inviteToken;
    }

    public String getSetupUrl() {
        return setupUrl;
    }

    public void setSetupUrl(String setupUrl) {
        this.setupUrl = setupUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
