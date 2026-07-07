package com.gabcompany.delivery_tracker.dto;

import com.gabcompany.delivery_tracker.model.Delivery;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DeliveryResponseDTO {

    private String trackingCode;
    private String recipient;
    private String Status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DeliveryResponseDTO(Delivery delivery) {
        this.trackingCode = delivery.getTrackingCode();
        this.recipient = delivery.getRecipient();
        Status = delivery.getStatus();
        this.createdAt = delivery.getCreateAt();
        this.updatedAt = delivery.getUpdateAt();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getTrackingCode() {
        return trackingCode;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getStatus() {
        return Status;
    }
}
