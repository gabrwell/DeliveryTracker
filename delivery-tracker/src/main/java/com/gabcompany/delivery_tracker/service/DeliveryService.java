package com.gabcompany.delivery_tracker.service;

import com.gabcompany.delivery_tracker.model.Delivery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DeliveryService {

    private List<Delivery> deliveries = new ArrayList<>();

    public Delivery createDelivery(String recipient){
        String trackingCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Delivery newDelivery = new Delivery(trackingCode, recipient, "CREATED");
        deliveries.add(newDelivery);

        return newDelivery;
    }

    public List<Delivery> getAllDeliveries() {
        return deliveries;
    }

}
