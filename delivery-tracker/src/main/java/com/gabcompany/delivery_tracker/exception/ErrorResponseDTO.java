package com.gabcompany.delivery_tracker.exception;

import java.time.LocalDateTime;

public record ErrorResponseDTO(LocalDateTime timestamp,
                               Integer status,
                               String message) {
}
