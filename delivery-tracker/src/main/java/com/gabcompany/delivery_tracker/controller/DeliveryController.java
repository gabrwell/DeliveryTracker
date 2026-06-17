package com.gabcompany.delivery_tracker.controller;


import com.gabcompany.delivery_tracker.model.Delivery;
import com.gabcompany.delivery_tracker.service.DeliveryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;


    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @PostMapping
    public Delivery createDelivery(@RequestBody Delivery deliveryRequest) {
        return deliveryService.createDelivery(deliveryRequest.getRecipient());
    }

    @GetMapping
    public List<Delivery> getAllDeliveries() {
        return deliveryService.getAllDeliveries();
    }
}
