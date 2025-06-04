package com.example.demo.entity;

public enum PaymentType {
    CASH("cash"),
    CARD("card"),
    DIGITAL("digital"),
    CREDIT_CARD("credit_card"),
    DEBIT_CARD("debit_card"),
    BANK_TRANSFER("bank_transfer"),
    E_WALLET("e_wallet"),
    VIRTUAL_ACCOUNT("virtual_account");
    
    private final String value;
    
    PaymentType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static PaymentType fromString(String value) {
        for (PaymentType type : PaymentType.values()) {
            if (type.value.equalsIgnoreCase(value) || type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid payment type: " + value);
    }
}
