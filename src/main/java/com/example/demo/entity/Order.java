package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders", schema = "public")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;
      @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonManagedReference("user-orders")
    private User user;
    
    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate = LocalDateTime.now();
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonManagedReference("order-orderDetails")
    private List<OrderDetail> orderDetails;@OneToMany(mappedBy = "order")
    @JsonManagedReference("order-payments")
    private List<Payment> payments;
    
    @OneToMany(mappedBy = "order")
    @JsonManagedReference("order-promotions")
    private List<OrderPromotion> orderPromotions;
    
    @OneToMany(mappedBy = "order")
    @JsonManagedReference("order-reviews")
    private List<Review> reviews;
    
    // Constructors
    public Order() {}
    
    public Order(User user, OrderStatus status) {
        this.user = user;
        this.status = status;
    }
    
    // Getters and Setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    
    public List<OrderDetail> getOrderDetails() { return orderDetails; }
    public void setOrderDetails(List<OrderDetail> orderDetails) { this.orderDetails = orderDetails; }
    
    public List<Payment> getPayments() { return payments; }
    public void setPayments(List<Payment> payments) { this.payments = payments; }
    
    public List<OrderPromotion> getOrderPromotions() { return orderPromotions; }
    public void setOrderPromotions(List<OrderPromotion> orderPromotions) { this.orderPromotions = orderPromotions; }
    
    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }
}
