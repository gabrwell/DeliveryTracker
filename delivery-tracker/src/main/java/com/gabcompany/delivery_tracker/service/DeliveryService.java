package com.gabcompany.delivery_tracker.service;

import com.gabcompany.delivery_tracker.exception.DeliveryNotFoundException;
import com.gabcompany.delivery_tracker.model.Delivery;
import com.gabcompany.delivery_tracker.model.DeliveryStatus;
import com.gabcompany.delivery_tracker.repository.DeliveryRepository;
import org.springframework.data.domain.Page;

import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public DeliveryService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }


    public Delivery createDelivery(String recipient){
        String trackingCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Delivery newDelivery = new Delivery(trackingCode, recipient, DeliveryStatus.CREATED);
        return deliveryRepository.save(newDelivery);

    }



    public Page<Delivery> getAllDeliveries(Pageable pageable) {
        return deliveryRepository.findAll(pageable);
    }

    public Delivery getDeliveryByTrackingCode(String trackingCode) {
        return deliveryRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new RuntimeException("Delivery not found with code" + trackingCode));
    }


    public Delivery updateDeliveryStatus(String trackingCode, String newStatus) {
        Delivery delivery = getDeliveryByTrackingCode(trackingCode);

        DeliveryStatus statusEnum = DeliveryStatus.valueOf(newStatus.toUpperCase());

        if (statusEnum == DeliveryStatus.DELIVERED) {
            delivery.setDeliveredAt(java.time.LocalDateTime.now());
        }

        delivery.setStatus(statusEnum);

        return deliveryRepository.save(delivery);
    }


}
