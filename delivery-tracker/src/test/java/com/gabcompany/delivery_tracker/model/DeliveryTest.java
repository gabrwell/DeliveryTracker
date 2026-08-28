package com.gabcompany.delivery_tracker.model;

import com.gabcompany.delivery_tracker.exception.InvalidStatusTransitionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeliveryTest {

    @Test
    void shouldStartWithCreatedStatus() {
        Delivery delivery = newDelivery();

        assertEquals(DeliveryStatus.CREATED, delivery.getStatus());
        assertNull(delivery.getDeliveredAt());
    }

    @ParameterizedTest
    @MethodSource("allowedTransitions")
    void shouldAllowValidStatusTransitions(DeliveryStatus currentStatus, DeliveryStatus requestedStatus) {
        Delivery delivery = deliveryWithStatus(currentStatus);

        boolean changed = delivery.changeStatus(requestedStatus);

        assertTrue(changed);
        assertEquals(requestedStatus, delivery.getStatus());
    }

    @Test
    void shouldSetDeliveredAtWhenDeliveryIsDelivered() {
        Delivery delivery = deliveryWithStatus(DeliveryStatus.IN_TRANSIT);

        delivery.changeStatus(DeliveryStatus.DELIVERED);

        assertNotNull(delivery.getDeliveredAt());
    }

    @ParameterizedTest
    @MethodSource("allStatuses")
    void shouldBeIdempotentWhenStatusDoesNotChange(DeliveryStatus status) {
        Delivery delivery = deliveryWithStatus(status);
        LocalDateTime originalDeliveredAt = delivery.getDeliveredAt();

        boolean changed = delivery.changeStatus(status);

        assertFalse(changed);
        assertEquals(status, delivery.getStatus());
        assertSame(originalDeliveredAt, delivery.getDeliveredAt());
    }

    @ParameterizedTest
    @MethodSource("invalidTransitions")
    void shouldRejectInvalidStatusTransitions(DeliveryStatus currentStatus, DeliveryStatus requestedStatus) {
        Delivery delivery = deliveryWithStatus(currentStatus);

        InvalidStatusTransitionException exception = assertThrows(
                InvalidStatusTransitionException.class,
                () -> delivery.changeStatus(requestedStatus));

        assertEquals(currentStatus, delivery.getStatus());
        assertEquals(
                "Cannot change delivery status from " + currentStatus + " to " + requestedStatus + ".",
                exception.getMessage());
    }

    private static Stream<Arguments> allowedTransitions() {
        return Stream.of(
                Arguments.of(DeliveryStatus.CREATED, DeliveryStatus.IN_TRANSIT),
                Arguments.of(DeliveryStatus.CREATED, DeliveryStatus.CANCELED),
                Arguments.of(DeliveryStatus.IN_TRANSIT, DeliveryStatus.DELIVERED),
                Arguments.of(DeliveryStatus.IN_TRANSIT, DeliveryStatus.CANCELED));
    }

    private static Stream<DeliveryStatus> allStatuses() {
        return Stream.of(DeliveryStatus.values());
    }

    private static Stream<Arguments> invalidTransitions() {
        return Stream.of(
                Arguments.of(DeliveryStatus.CREATED, DeliveryStatus.DELIVERED),
                Arguments.of(DeliveryStatus.IN_TRANSIT, DeliveryStatus.CREATED),
                Arguments.of(DeliveryStatus.DELIVERED, DeliveryStatus.CREATED),
                Arguments.of(DeliveryStatus.DELIVERED, DeliveryStatus.IN_TRANSIT),
                Arguments.of(DeliveryStatus.DELIVERED, DeliveryStatus.CANCELED),
                Arguments.of(DeliveryStatus.CANCELED, DeliveryStatus.CREATED),
                Arguments.of(DeliveryStatus.CANCELED, DeliveryStatus.IN_TRANSIT),
                Arguments.of(DeliveryStatus.CANCELED, DeliveryStatus.DELIVERED));
    }

    private static Delivery newDelivery() {
        return new Delivery("ABC123", "GABRIEL");
    }

    private static Delivery deliveryWithStatus(DeliveryStatus status) {
        Delivery delivery = newDelivery();

        if (status == DeliveryStatus.IN_TRANSIT || status == DeliveryStatus.DELIVERED) {
            delivery.changeStatus(DeliveryStatus.IN_TRANSIT);
        }
        if (status == DeliveryStatus.DELIVERED) {
            delivery.changeStatus(DeliveryStatus.DELIVERED);
        }
        if (status == DeliveryStatus.CANCELED) {
            delivery.changeStatus(DeliveryStatus.CANCELED);
        }

        return delivery;
    }
}
