package com.gabcompany.delivery_tracker.repository;

import com.gabcompany.delivery_tracker.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {


}
