package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "ordercustomizations", schema = "public")
public class OrderCustomization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_customization_id")
    private Long orderCustomizationId;      @ManyToOne
    @JoinColumn(name = "detail_id", nullable = false)
    @JsonBackReference("orderDetail-customizations")
    private OrderDetail orderDetail;
    
    @ManyToOne
    @JoinColumn(name = "customization_id", nullable = false)
    private Customization customization;
    
    // Constructors
    public OrderCustomization() {}
    
    public OrderCustomization(OrderDetail orderDetail, Customization customization) {
        this.orderDetail = orderDetail;
        this.customization = customization;
    }
    
    // Getters and Setters
    public Long getOrderCustomizationId() { return orderCustomizationId; }
    public void setOrderCustomizationId(Long orderCustomizationId) { this.orderCustomizationId = orderCustomizationId; }
    
    public OrderDetail getOrderDetail() { return orderDetail; }
    public void setOrderDetail(OrderDetail orderDetail) { this.orderDetail = orderDetail; }
    
    public Customization getCustomization() { return customization; }
    public void setCustomization(Customization customization) { this.customization = customization; }
}
