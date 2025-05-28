package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments", schema = "public")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;
      @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnoreProperties("payments")
    private Order order;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType type;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;
    
    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate = LocalDateTime.now();
    
    @Column(name = "transaction_id", length = 100)
    private String transactionId;
    
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;
    
    @Column(name = "fraud_status", length = 20)
    private String fraudStatus;
    
    @Column(name = "bank", length = 50)
    private String bank;
    
    @Column(name = "va_number", length = 50)
    private String vaNumber;
    
    @Column(name = "three_ds")
    private Boolean threeDs = false;
      // Constructors
    public Payment() {}
    
    public Payment(Order order, PaymentType type, BigDecimal amount, PaymentStatus status) {
        this.order = order;
        this.type = type;
        this.amount = amount;
        this.status = status;
    }
    
    public Payment(Order order, PaymentType type, BigDecimal amount, PaymentStatus status, 
                  String paymentMethod, String bank, String vaNumber) {
        this.order = order;
        this.type = type;
        this.amount = amount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.bank = bank;
        this.vaNumber = vaNumber;
    }
    
    // Getters and Setters
    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
    
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    
    public PaymentType getType() { return type; }
    public void setType(PaymentType type) { this.type = type; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
    
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getFraudStatus() { return fraudStatus; }
    public void setFraudStatus(String fraudStatus) { this.fraudStatus = fraudStatus; }
    
    public String getBank() { return bank; }
    public void setBank(String bank) { this.bank = bank; }
    
    public String getVaNumber() { return vaNumber; }
    public void setVaNumber(String vaNumber) { this.vaNumber = vaNumber; }
    
    public Boolean getThreeDs() { return threeDs; }
    public void setThreeDs(Boolean threeDs) { this.threeDs = threeDs; }
}
