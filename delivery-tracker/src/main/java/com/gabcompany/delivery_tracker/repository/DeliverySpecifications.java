package com.gabcompany.delivery_tracker.repository;

import com.gabcompany.delivery_tracker.model.Delivery;
import com.gabcompany.delivery_tracker.model.DeliveryStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Locale;

public final class DeliverySpecifications {

    private DeliverySpecifications() {
    }

    public static Specification<Delivery> hasStatus(DeliveryStatus status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Delivery> recipientContains(String recipient) {
        String pattern = "%" + recipient.trim().toLowerCase(Locale.ROOT) + "%";

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("recipient")), pattern);
    }

    public static Specification<Delivery> createdAtOrAfter(LocalDateTime createdFrom) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("createAt"), createdFrom);
    }

    public static Specification<Delivery> createdAtOrBefore(LocalDateTime createdTo) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("createAt"), createdTo);
    }
}
