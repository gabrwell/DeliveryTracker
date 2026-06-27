package com.gabcompany.delivery_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DeliveryRequestDto {

    @NotBlank(message = "Recipient name is required and cannot be blank.")

    @Size(min = 3, message = "Recipient name must be at least 3 characters long.")

    private String recipient;

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}
