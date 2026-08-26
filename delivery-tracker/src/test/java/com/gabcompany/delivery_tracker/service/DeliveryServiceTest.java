package com.gabcompany.delivery_tracker.service;

import com.gabcompany.delivery_tracker.exception.DeliveryNotFoundException;
import com.gabcompany.delivery_tracker.exception.InvalidDeliveryStatusException;
import com.gabcompany.delivery_tracker.model.Delivery;
import com.gabcompany.delivery_tracker.model.DeliveryStatus;
import com.gabcompany.delivery_tracker.repository.DeliveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeliveryServiceTest {

    private DeliveryRepository deliveryRepository;
    private DeliveryService deliveryService;

    @BeforeEach
    void setUp() {
        deliveryRepository = mock(DeliveryRepository.class);
        deliveryService = new DeliveryService(deliveryRepository);
    }

    @Test
    void shouldThrowDeliveryNotFoundExceptionWhenTrackingCodeDoesNotExist() {
        when(deliveryRepository.findByTrackingCode("UNKNOWN"))
                .thenReturn(Optional.empty());

        DeliveryNotFoundException exception = assertThrows(
                DeliveryNotFoundException.class,
                () -> deliveryService.getDeliveryByTrackingCode("UNKNOWN"));

        assertEquals("Delivery not found with tracking code: UNKNOWN", exception.getMessage());
    }

    @Test
    void shouldThrowInvalidDeliveryStatusExceptionWhenStatusDoesNotExist() {
        Delivery delivery = new Delivery("ABC123", "GABRIEL", DeliveryStatus.CREATED);
        when(deliveryRepository.findByTrackingCode("ABC123"))
                .thenReturn(Optional.of(delivery));

        assertThrows(
                InvalidDeliveryStatusException.class,
                () -> deliveryService.updateDeliveryStatus("ABC123", "LOST"));

        verify(deliveryRepository, never()).save(delivery);
    }
}
