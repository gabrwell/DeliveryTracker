package com.gabcompany.delivery_tracker.repository;

import com.gabcompany.delivery_tracker.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long>, JpaSpecificationExecutor<Delivery> {

    Optional<Delivery> findByTrackingCode(String trackingCode);


}
