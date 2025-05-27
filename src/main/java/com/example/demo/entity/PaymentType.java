package com.example.demo.entity;

public enum PaymentType {
    CASH("cash"),
    CARD("card"),
    DIGITAL("digital");
    
    private final String value;
    
    PaymentType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}
