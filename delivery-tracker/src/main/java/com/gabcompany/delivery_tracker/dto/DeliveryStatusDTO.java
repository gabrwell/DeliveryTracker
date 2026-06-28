package com.gabcompany.delivery_tracker.dto;

import jakarta.validation.constraints.NotBlank;

public class DeliveryStatusDTO {

    @NotBlank(message = "Status is required and cannot be blank. ")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
