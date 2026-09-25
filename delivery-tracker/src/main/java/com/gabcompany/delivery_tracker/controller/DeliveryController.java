package com.gabcompany.delivery_tracker.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


import com.gabcompany.delivery_tracker.dto.DeliveryFilter;
import com.gabcompany.delivery_tracker.dto.DeliveryRequestDTO;
import com.gabcompany.delivery_tracker.dto.DeliveryResponseDTO;
import com.gabcompany.delivery_tracker.dto.DeliveryStatusDTO;
import com.gabcompany.delivery_tracker.dto.DeliveryStatusHistoryResponseDTO;
import com.gabcompany.delivery_tracker.model.Delivery;
import com.gabcompany.delivery_tracker.service.DeliveryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/deliveries")
@CrossOrigin(origins = "${app.cors.allowed-origins}")
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

        dto.add(linkTo(methodOn(DeliveryController.class)
                .getAllDeliveries(null, null, null, null, null))
                .withRel("all_deliveries"));

        dto.add(linkTo(methodOn(DeliveryController.class).getDeliveryStatusHistory(trackingCode))
                .withRel("status_history"));

        return dto;
    }

    @GetMapping("/{trackingCode}/history")
    public List<DeliveryStatusHistoryResponseDTO> getDeliveryStatusHistory(
            @PathVariable String trackingCode) {
        return deliveryService.getDeliveryStatusHistory(trackingCode).stream()
                .map(DeliveryStatusHistoryResponseDTO::new)
                .toList();
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
    public Page<DeliveryResponseDTO> getAllDeliveries(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String recipient,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdTo,
            @PageableDefault(size = 10, page = 0, sort = "trackingCode") Pageable pageable) {

        DeliveryFilter filter = new DeliveryFilter(status, recipient, createdFrom, createdTo);

        Page<Delivery> deliveriesPage = deliveryService.getAllDeliveries(filter, pageable);

        return deliveriesPage.map(DeliveryResponseDTO::new);
    }
}
