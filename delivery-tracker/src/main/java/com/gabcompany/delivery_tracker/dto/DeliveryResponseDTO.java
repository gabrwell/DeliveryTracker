package com.gabcompany.delivery_tracker.dto;

import com.gabcompany.delivery_tracker.model.Delivery;

public class DeliveryResponseDTO {

    private String trackingCode;
    private String recipient;
    private String Status;

    public DeliveryResponseDTO(Delivery delivery) {
        this.trackingCode = delivery.getTrackingCode();
        this.recipient = delivery.getRecipient();
        Status = delivery.getStatus();
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
