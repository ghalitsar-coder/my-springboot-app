package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "customizations", schema = "public")
public class Customization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customization_id")
    private Long customizationId;
    
    @Column(nullable = false, length = 50)
    private String name;
    
    @Column(name = "price_adjustment", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceAdjustment = BigDecimal.ZERO;
    
    @Column(columnDefinition = "TEXT")
    private String description;      @OneToMany(mappedBy = "customization")
    @JsonIgnore
    private List<OrderCustomization> orderCustomizations;
    
    // Constructors
    public Customization() {}
    
    public Customization(String name, BigDecimal priceAdjustment, String description) {
        this.name = name;
        this.priceAdjustment = priceAdjustment;
        this.description = description;
    }
    
    // Getters and Setters
    public Long getCustomizationId() { return customizationId; }
    public void setCustomizationId(Long customizationId) { this.customizationId = customizationId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public BigDecimal getPriceAdjustment() { return priceAdjustment; }
    public void setPriceAdjustment(BigDecimal priceAdjustment) { this.priceAdjustment = priceAdjustment; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public List<OrderCustomization> getOrderCustomizations() { return orderCustomizations; }
    public void setOrderCustomizations(List<OrderCustomization> orderCustomizations) { this.orderCustomizations = orderCustomizations; }
}
