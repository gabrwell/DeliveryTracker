package com.gabcompany.delivery_tracker.service;

import com.gabcompany.delivery_tracker.dto.DeliveryFilter;
import com.gabcompany.delivery_tracker.exception.DeliveryNotFoundException;
import com.gabcompany.delivery_tracker.exception.InvalidDeliveryFilterException;
import com.gabcompany.delivery_tracker.exception.InvalidDeliveryStatusException;
import com.gabcompany.delivery_tracker.model.Delivery;
import com.gabcompany.delivery_tracker.model.DeliveryStatus;
import com.gabcompany.delivery_tracker.model.DeliveryStatusHistory;
import com.gabcompany.delivery_tracker.repository.DeliveryRepository;
import com.gabcompany.delivery_tracker.repository.DeliverySpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public DeliveryService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }


    public Delivery createDelivery(String recipient){
        String trackingCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Delivery newDelivery = new Delivery(trackingCode, recipient);
        return deliveryRepository.save(newDelivery);

    }



    @Transactional(readOnly = true)
    public Page<Delivery> getAllDeliveries(DeliveryFilter filter, Pageable pageable) {
        validateDateRange(filter);

        Specification<Delivery> specification = Specification.unrestricted();

        if (filter.status() != null && !filter.status().isBlank()) {
            specification = specification.and(
                    DeliverySpecifications.hasStatus(parseStatus(filter.status())));
        }

        if (filter.recipient() != null && !filter.recipient().isBlank()) {
            specification = specification.and(
                    DeliverySpecifications.recipientContains(filter.recipient()));
        }

        if (filter.createdFrom() != null) {
            specification = specification.and(
                    DeliverySpecifications.createdAtOrAfter(filter.createdFrom()));
        }

        if (filter.createdTo() != null) {
            specification = specification.and(
                    DeliverySpecifications.createdAtOrBefore(filter.createdTo()));
        }

        return deliveryRepository.findAll(specification, pageable);
    }

    public Delivery getDeliveryByTrackingCode(String trackingCode) {
        return deliveryRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new DeliveryNotFoundException(
                        "Delivery not found with tracking code: " + trackingCode));
    }

    @Transactional(readOnly = true)
    public List<DeliveryStatusHistory> getDeliveryStatusHistory(String trackingCode) {
        Delivery delivery = getDeliveryByTrackingCode(trackingCode);
        return List.copyOf(delivery.getStatusHistory());
    }


    @Transactional
    public Delivery updateDeliveryStatus(String trackingCode, String newStatus) {
        Delivery delivery = getDeliveryByTrackingCode(trackingCode);
        DeliveryStatus statusEnum = parseStatus(newStatus);

        boolean changed = delivery.changeStatus(statusEnum);
        if (!changed) {
            return delivery;
        }

        return deliveryRepository.save(delivery);
    }

    private DeliveryStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new InvalidDeliveryStatusException(status);
        }

        try {
            return DeliveryStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new InvalidDeliveryStatusException(status);
        }
    }

    private void validateDateRange(DeliveryFilter filter) {
        if (filter.createdFrom() != null
                && filter.createdTo() != null
                && filter.createdFrom().isAfter(filter.createdTo())) {
            throw new InvalidDeliveryFilterException(
                    "createdFrom must be before or equal to createdTo.");
        }
    }

}
