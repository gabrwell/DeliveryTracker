package com.gabcompany.delivery_tracker.model;

import com.gabcompany.delivery_tracker.exception.InvalidStatusTransitionException;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String trackingCode;

    @Column(nullable = false, length = 100)
    private String recipient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DeliveryStatus status;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;


    protected Delivery() {

    }

    public Delivery(String trackingCode, String recipient) {
        this.trackingCode = trackingCode;
        this.recipient = recipient;
        this.status = DeliveryStatus.CREATED;
    }

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updateAt;

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrackingCode() {
        return trackingCode;
    }

    public void setTrackingCode(String trackingCode) {
        this.trackingCode = trackingCode;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public boolean changeStatus(DeliveryStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("New delivery status must not be null.");
        }

        if (status == newStatus) {
            return false;
        }

        if (!canTransitionTo(newStatus)) {
            throw new InvalidStatusTransitionException(status, newStatus);
        }

        status = newStatus;
        if (newStatus == DeliveryStatus.DELIVERED) {
            deliveredAt = LocalDateTime.now();
        }

        return true;
    }

    private boolean canTransitionTo(DeliveryStatus newStatus) {
        return switch (status) {
            case CREATED -> newStatus == DeliveryStatus.IN_TRANSIT
                    || newStatus == DeliveryStatus.CANCELED;
            case IN_TRANSIT -> newStatus == DeliveryStatus.DELIVERED
                    || newStatus == DeliveryStatus.CANCELED;
            case DELIVERED, CANCELED -> false;
        };
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

}
