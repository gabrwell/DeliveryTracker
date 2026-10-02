package com.gabcompany.delivery_tracker.controller;

import com.gabcompany.delivery_tracker.dto.TrackingResponseDTO;
import com.gabcompany.delivery_tracker.service.DeliveryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@RequestMapping("/tracking")
public class TrackingController {

    private final DeliveryService deliveryService;

    public TrackingController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping("/{trackingCode}")
    public TrackingResponseDTO trackDelivery(@PathVariable String trackingCode) {
        String normalizedCode = trackingCode.trim().toUpperCase(Locale.ROOT);
        return new TrackingResponseDTO(deliveryService.getDeliveryByTrackingCode(normalizedCode));
    }
}
