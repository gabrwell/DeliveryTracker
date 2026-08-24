package com.gabcompany.delivery_tracker.dto;

import com.gabcompany.delivery_tracker.model.Delivery;
import org.springframework.hateoas.RepresentationModel;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DeliveryResponseDTO extends RepresentationModel<DeliveryResponseDTO> {

    private String trackingCode;
    private String recipient;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime returnDeadline;

    public DeliveryResponseDTO(Delivery delivery) {
        this.trackingCode = delivery.getTrackingCode();
        this.recipient = delivery.getRecipient();
        this.status = delivery.getStatus().name();
        this.createdAt = delivery.getCreateAt();
        this.updatedAt = delivery.getUpdateAt();
        this.deliveredAt = delivery.getDeliveredAt();
        this.returnDeadline = calculateReturnDeadline(this.deliveredAt);    }

    private LocalDateTime calculateReturnDeadline(LocalDateTime deliveryDate) {
        if (deliveryDate == null) return null;

        LocalDateTime deadline = deliveryDate;
        int addedDays = 0;

        while (addedDays < 7) {
            deadline = deadline.plusDays(1);
            if (deadline.getDayOfWeek() != DayOfWeek.SATURDAY &&
                    deadline.getDayOfWeek() != DayOfWeek.SUNDAY) {
                addedDays++;
            }
        }
        return deadline;
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

    public LocalDateTime getReturnDeadline() {
        return returnDeadline;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }
}
