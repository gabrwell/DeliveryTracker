package com.gabcompany.delivery_tracker.controller;


import com.gabcompany.delivery_tracker.dto.DeliveryRequestDto;
import com.gabcompany.delivery_tracker.dto.DeliveryResponseDTO;
import com.gabcompany.delivery_tracker.model.Delivery;
import com.gabcompany.delivery_tracker.service.DeliveryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;


    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }





    @GetMapping("/{trackingCode}")
    public Delivery getDeliveryByCode(@PathVariable String trackingCode) {
        return deliveryService.getDeliveryByCode(trackingCode);
    }

    @PatchMapping("/{trackingCode}/status")
    public Delivery updateStatus(@PathVariable String trackingCode, @RequestBody Map<String, String> requestBody) {
        String newStatus = requestBody.get("status");
        return deliveryService.updateDeliveryStatus(trackingCode, newStatus);
    }

    @PostMapping
    public DeliveryResponseDTO createDelivery(@Valid @RequestBody DeliveryRequestDto request) {
        Delivery savedDelivery = deliveryService.createDelivery(request.getRecipient());
        return new DeliveryResponseDTO(savedDelivery);
    }

    @GetMapping
    public List<DeliveryResponseDTO> getAllDeliveries() {
        List<Delivery> deliveries = deliveryService.getAllDeliveries();

        return deliveries.stream()
        .map(DeliveryResponseDTO::new)
        .toList();
    }

}
