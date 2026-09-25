package com.gabcompany.delivery_tracker.dto;

import java.time.LocalDateTime;

public record DeliveryFilter(
        String status,
        String recipient,
        LocalDateTime createdFrom,
        LocalDateTime createdTo) {
}
