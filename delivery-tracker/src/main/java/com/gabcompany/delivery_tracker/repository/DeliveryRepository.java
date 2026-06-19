package com.gabcompany.delivery_tracker.repository;

import com.gabcompany.delivery_tracker.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Optional<Delivery> findByTrackingCode(String trackingCode);


}
