package com.example.demo.entity;

public enum PaymentStatus {
    PENDING("pending"),
    COMPLETED("completed"),
    FAILED("failed"),
    SETTLEMENT("settlement"),
    CAPTURE("capture"),
    DENY("deny"),
    CANCEL("cancel"),
    EXPIRE("expire"),
    REFUND("refund"),
    PARTIAL_REFUND("partial_refund"),
    AUTHORIZED("authorized");
    
    private final String value;
    
    PaymentStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static PaymentStatus fromString(String value) {
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.value.equalsIgnoreCase(value) || status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        // Default fallback mapping for Midtrans statuses
        switch (value.toLowerCase()) {
            case "settlement":
            case "capture":
                return COMPLETED;
            case "deny":
            case "cancel":
            case "expire":
            case "failure":
                return FAILED;
            default:
                return PENDING;
        }
    }
}
