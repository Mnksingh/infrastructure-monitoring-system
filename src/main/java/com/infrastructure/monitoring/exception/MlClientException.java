package com.infrastructure.monitoring.exception;

import org.springframework.http.HttpStatus;

public class MlClientException extends RuntimeException {

    private final HttpStatus status;

    public MlClientException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public MlClientException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
