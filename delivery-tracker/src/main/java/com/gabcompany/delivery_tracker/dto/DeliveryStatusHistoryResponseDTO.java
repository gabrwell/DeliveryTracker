package com.gabcompany.delivery_tracker.dto;

import com.gabcompany.delivery_tracker.model.DeliveryStatus;
import com.gabcompany.delivery_tracker.model.DeliveryStatusHistory;

import java.time.LocalDateTime;

public record DeliveryStatusHistoryResponseDTO(
        String previousStatus,
        String newStatus,
        LocalDateTime changedAt) {

    public DeliveryStatusHistoryResponseDTO(DeliveryStatusHistory history) {
        this(
                statusName(history.getPreviousStatus()),
                history.getNewStatus().name(),
                history.getChangedAt());
    }

    private static String statusName(DeliveryStatus status) {
        return status == null ? null : status.name();
    }
}
