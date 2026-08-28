package com.gabcompany.delivery_tracker.exception;

import com.gabcompany.delivery_tracker.model.DeliveryStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldReturnNotFoundWhenDeliveryDoesNotExist() {
        DeliveryNotFoundException exception =
                new DeliveryNotFoundException("Delivery not found with tracking code: UNKNOWN");

        ResponseEntity<StandardError> response = handler.handleDeliveryNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals(exception.getMessage(), response.getBody().getMessage());
    }

    @Test
    void shouldReturnBadRequestWhenDeliveryStatusIsInvalid() {
        InvalidDeliveryStatusException exception = new InvalidDeliveryStatusException("LOST");

        ResponseEntity<StandardError> response = handler.handleInvalidDeliveryStatus(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals(exception.getMessage(), response.getBody().getMessage());
    }

    @Test
    void shouldReturnConflictWhenStatusTransitionIsInvalid() {
        InvalidStatusTransitionException exception = new InvalidStatusTransitionException(
                DeliveryStatus.CREATED,
                DeliveryStatus.DELIVERED);

        ResponseEntity<StandardError> response = handler.handleInvalidStatusTransition(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getStatus());
        assertEquals(exception.getMessage(), response.getBody().getMessage());
    }
}
