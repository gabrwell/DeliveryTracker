package com.gabcompany.delivery_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DeliveryRequestDTO(@NotBlank(message = "Recipient name is required and cannot be blank.")
                                 @Size(min = 3, message = "Recipient name must be at least 3 characters long.")
                                 String recipient) {

    public DeliveryRequestDTO {
        if (recipient != null) {
            recipient = recipient.trim().toUpperCase();
        }
    }
}


