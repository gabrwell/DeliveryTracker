package com.gabcompany.delivery_tracker.controller;


import com.gabcompany.delivery_tracker.dto.DeliveryRequestDto;
import com.gabcompany.delivery_tracker.dto.DeliveryResponseDTO;
import com.gabcompany.delivery_tracker.dto.DeliveryStatusDTO;
import com.gabcompany.delivery_tracker.model.Delivery;
import com.gabcompany.delivery_tracker.service.DeliveryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
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
    public DeliveryResponseDTO updateStatus(@PathVariable String trackingCode, @Valid @RequestBody DeliveryStatusDTO requestBody) {

        String newStatus = requestBody.getStatus();
        Delivery updatedDelivery = deliveryService.updateDeliveryStatus(trackingCode, newStatus);

        return new DeliveryResponseDTO(updatedDelivery);
    }

    @PostMapping
    public DeliveryResponseDTO createDelivery(@Valid @RequestBody DeliveryRequestDto request) {
        Delivery savedDelivery = deliveryService.createDelivery(request.getRecipient());
        return new DeliveryResponseDTO(savedDelivery);
    }

    @GetMapping
    public Page<DeliveryResponseDTO> getAllDeliveries(@PageableDefault(size = 10, page = 0, sort = "trackingCode") Pageable pageable) {

        Page<Delivery> deliveriesPage = deliveryService.getAllDeliveries(pageable);

        return deliveriesPage.map(DeliveryResponseDTO::new);
    }
}
