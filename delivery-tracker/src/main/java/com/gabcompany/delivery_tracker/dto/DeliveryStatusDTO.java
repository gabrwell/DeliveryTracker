package com.gabcompany.delivery_tracker.dto;

import jakarta.validation.constraints.NotBlank;

public record DeliveryStatusDTO(@NotBlank(message = "Status is required and cannot be blank.")
                                String status) {


}
