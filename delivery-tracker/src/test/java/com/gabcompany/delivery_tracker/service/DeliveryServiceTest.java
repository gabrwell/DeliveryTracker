package com.gabcompany.delivery_tracker.service;

import com.gabcompany.delivery_tracker.exception.DeliveryNotFoundException;
import com.gabcompany.delivery_tracker.exception.InvalidDeliveryStatusException;
import com.gabcompany.delivery_tracker.exception.InvalidStatusTransitionException;
import com.gabcompany.delivery_tracker.model.Delivery;
import com.gabcompany.delivery_tracker.model.DeliveryStatus;
import com.gabcompany.delivery_tracker.repository.DeliveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
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
        Delivery delivery = new Delivery("ABC123", "GABRIEL");
        when(deliveryRepository.findByTrackingCode("ABC123"))
                .thenReturn(Optional.of(delivery));

        assertThrows(
                InvalidDeliveryStatusException.class,
                () -> deliveryService.updateDeliveryStatus("ABC123", "LOST"));

        verify(deliveryRepository, never()).save(delivery);
    }

    @Test
    void shouldRejectInvalidStatusTransitionWithoutSaving() {
        Delivery delivery = new Delivery("ABC123", "GABRIEL");
        when(deliveryRepository.findByTrackingCode("ABC123"))
                .thenReturn(Optional.of(delivery));

        assertThrows(
                InvalidStatusTransitionException.class,
                () -> deliveryService.updateDeliveryStatus("ABC123", "DELIVERED"));

        assertEquals(DeliveryStatus.CREATED, delivery.getStatus());
        verify(deliveryRepository, never()).save(delivery);
    }

    @Test
    void shouldNotSaveWhenRequestedStatusIsTheCurrentStatus() {
        Delivery delivery = new Delivery("ABC123", "GABRIEL");
        when(deliveryRepository.findByTrackingCode("ABC123"))
                .thenReturn(Optional.of(delivery));

        Delivery result = deliveryService.updateDeliveryStatus("ABC123", "created");

        assertSame(delivery, result);
        verify(deliveryRepository, never()).save(delivery);
    }

    @Test
    void shouldSaveWhenStatusTransitionIsValid() {
        Delivery delivery = new Delivery("ABC123", "GABRIEL");
        when(deliveryRepository.findByTrackingCode("ABC123"))
                .thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(delivery)).thenReturn(delivery);

        Delivery result = deliveryService.updateDeliveryStatus("ABC123", "in_transit");

        assertSame(delivery, result);
        assertEquals(DeliveryStatus.IN_TRANSIT, delivery.getStatus());
        verify(deliveryRepository).save(delivery);
    }
}
