package com.gabcompany.delivery_tracker.dto;

import com.gabcompany.delivery_tracker.model.Delivery;
import com.gabcompany.delivery_tracker.model.DeliveryStatus;

import java.time.LocalDateTime;

public record TrackingResponseDTO(String trackingCode, DeliveryStatus status,
                                  LocalDateTime createdAt, LocalDateTime updatedAt,
                                  LocalDateTime deliveredAt) {

    public TrackingResponseDTO(Delivery delivery) {
        this(delivery.getTrackingCode(), delivery.getStatus(), delivery.getCreateAt(),
                delivery.getUpdateAt(), delivery.getDeliveredAt());
    }
}
