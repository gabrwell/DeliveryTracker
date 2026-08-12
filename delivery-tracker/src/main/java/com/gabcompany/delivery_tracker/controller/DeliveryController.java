package com.gabcompany.delivery_tracker.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


import com.gabcompany.delivery_tracker.dto.DeliveryRequestDTO;
import com.gabcompany.delivery_tracker.dto.DeliveryResponseDTO;
import com.gabcompany.delivery_tracker.dto.DeliveryStatusDTO;
import com.gabcompany.delivery_tracker.model.Delivery;
import com.gabcompany.delivery_tracker.service.DeliveryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/deliveries")
@CrossOrigin(origins = "http://localhost:4200")
public class DeliveryController {

    private final DeliveryService deliveryService;


    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }


    @GetMapping("/{trackingCode}")
    public DeliveryResponseDTO getDeliveryByCode(@PathVariable String trackingCode) {
        Delivery delivery = deliveryService.getDeliveryByTrackingCode(trackingCode);

        DeliveryResponseDTO dto = new DeliveryResponseDTO(delivery);

        dto.add(linkTo(methodOn(DeliveryController.class).getDeliveryByCode(trackingCode)).withSelfRel());

        dto.add(linkTo(methodOn(DeliveryController.class).getAllDeliveries(null)).withRel("all_deliveries"));

        return dto;
    }

    @PatchMapping("/{trackingCode}/status")
    public DeliveryResponseDTO updateStatus(@PathVariable String trackingCode, @Valid @RequestBody DeliveryStatusDTO requestBody) {

        String newStatus = requestBody.status();
        Delivery updatedDelivery = deliveryService.updateDeliveryStatus(trackingCode, newStatus);

        return new DeliveryResponseDTO(updatedDelivery);
    }

    @PostMapping
    public DeliveryResponseDTO createDelivery(@Valid @RequestBody DeliveryRequestDTO request) {
        Delivery savedDelivery = deliveryService.createDelivery(request.recipient());
        return new DeliveryResponseDTO(savedDelivery);
    }

    @GetMapping
    public Page<DeliveryResponseDTO> getAllDeliveries(@PageableDefault(size = 10, page = 0, sort = "trackingCode") Pageable pageable) {

        Page<Delivery> deliveriesPage = deliveryService.getAllDeliveries(pageable);

        return deliveriesPage.map(DeliveryResponseDTO::new);
    }
}
