package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "orderpromotions")
public class OrderPromotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_promotion_id")
    private Long orderPromotionId;
    
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @ManyToOne
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;
    
    // Constructors
    public OrderPromotion() {}
    
    public OrderPromotion(Order order, Promotion promotion) {
        this.order = order;
        this.promotion = promotion;
    }
    
    // Getters and Setters
    public Long getOrderPromotionId() { return orderPromotionId; }
    public void setOrderPromotionId(Long orderPromotionId) { this.orderPromotionId = orderPromotionId; }
    
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    
    public Promotion getPromotion() { return promotion; }
    public void setPromotion(Promotion promotion) { this.promotion = promotion; }
}
