package com.example.demo.entity;

public enum PaymentStatus {
    PENDING("pending"),
    COMPLETED("completed"),
    FAILED("failed");
    
    private final String value;
    
    PaymentStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}
