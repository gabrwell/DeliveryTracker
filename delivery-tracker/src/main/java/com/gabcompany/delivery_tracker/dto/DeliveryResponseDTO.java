package com.gabcompany.delivery_tracker.dto;

import com.gabcompany.delivery_tracker.model.Delivery;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DeliveryResponseDTO extends RepresentationModel<DeliveryResponseDTO> {

    private String trackingCode;
    private String recipient;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DeliveryResponseDTO(Delivery delivery) {
        this.trackingCode = delivery.getTrackingCode();
        this.recipient = delivery.getRecipient();
        this.status = delivery.getStatus();
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
        return status;
    }
}
