package com.gabcompany.delivery_tracker.exception;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class StandardError {

    private LocalDateTime timestamp;
    private Integer status;
    private String message;

    public StandardError(Integer status, String message) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public Integer getStatus() {
        return status;
    }
}
