package com.gabcompany.delivery_tracker.exception;

import com.gabcompany.delivery_tracker.model.DeliveryStatus;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(DeliveryStatus currentStatus, DeliveryStatus requestedStatus) {
        super("Cannot change delivery status from " + currentStatus + " to " + requestedStatus + ".");
    }
}
