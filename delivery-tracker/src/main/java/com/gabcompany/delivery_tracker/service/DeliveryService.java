package com.gabcompany.delivery_tracker.service;

import com.gabcompany.delivery_tracker.exception.DeliveryNotFoundException;
import com.gabcompany.delivery_tracker.model.Delivery;
import com.gabcompany.delivery_tracker.repository.DeliveryRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
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

        Delivery newDelivery = new Delivery(trackingCode, recipient, "CREATED");
        return deliveryRepository.save(newDelivery);

    }



    public Page<Delivery> getAllDeliveries(Pageable pageable) {
        return deliveryRepository.findAll(pageable);
    }

    public Delivery getDeliveryByCode(String trackingCode) {
        return deliveryRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new DeliveryNotFoundException("Entrega não encontrada com o código: " + trackingCode));
    }

    public Delivery updateDeliveryStatus(String trackingCode, String newStatus) {
        Delivery delivery = getDeliveryByCode(trackingCode);

        delivery.setStatus(newStatus);

        return deliveryRepository.save(delivery);
    }

}
