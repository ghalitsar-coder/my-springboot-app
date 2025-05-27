package com.example.demo.entity;

public enum OrderStatus {
    PENDING("pending"),
    PREPARED("prepared"),
    DELIVERED("delivered"),
    CANCELLED("cancelled");
    
    private final String value;
    
    OrderStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}
