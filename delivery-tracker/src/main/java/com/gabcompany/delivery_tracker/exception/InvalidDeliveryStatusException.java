package com.gabcompany.delivery_tracker.exception;

import com.gabcompany.delivery_tracker.model.DeliveryStatus;

import java.util.Arrays;

public class InvalidDeliveryStatusException extends RuntimeException {

    public InvalidDeliveryStatusException(String status) {
        super("Invalid delivery status: '" + status + "'. Allowed values: "
                + Arrays.toString(DeliveryStatus.values()));
    }
}
