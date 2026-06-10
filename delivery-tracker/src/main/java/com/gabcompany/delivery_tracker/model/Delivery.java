package com.gabcompany.delivery_tracker.model;

public class Delivery {

    public String trackingCode;
    public String recipient;
    public String status;

    public Delivery() {

    }

    public Delivery(String trackingCode, String recipient, String status) {
        this.trackingCode = trackingCode;
        this.recipient = recipient;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
