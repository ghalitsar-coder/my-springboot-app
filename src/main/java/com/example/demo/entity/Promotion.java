package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "promotions", schema = "public")
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotion_id")
    private Long promotionId;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
      @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    // Promotion rules/conditions
    @Column(name = "minimum_purchase_amount", precision = 10, scale = 2)
    private BigDecimal minimumPurchaseAmount;
    
    @Column(name = "maximum_uses")
    private Integer maximumUses;
    
    @Column(name = "current_uses")
    private Integer currentUses = 0;
    
    @Column(name = "promotion_type", length = 50)
    private String promotionType = "PERCENTAGE"; // PERCENTAGE, FIXED_AMOUNT
    
    @Column(name = "max_discount_amount", precision = 10, scale = 2)
    private BigDecimal maxDiscountAmount;
    
    @OneToMany(mappedBy = "promotion")
    @JsonIgnoreProperties("promotion")
    private List<OrderPromotion> orderPromotions;
    
    // Constructors
    public Promotion() {}
      public Promotion(String name, String description, BigDecimal discountValue, 
                    LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.description = description;
        this.discountValue = discountValue;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    public Promotion(String name, String description, BigDecimal discountValue, 
                    LocalDate startDate, LocalDate endDate, 
                    BigDecimal minimumPurchaseAmount, Integer maximumUses) {
        this.name = name;
        this.description = description;
        this.discountValue = discountValue;
        this.startDate = startDate;
        this.endDate = endDate;
        this.minimumPurchaseAmount = minimumPurchaseAmount;
        this.maximumUses = maximumUses;
    }
    
    // Getters and Setters
    public Long getPromotionId() { return promotionId; }
    public void setPromotionId(Long promotionId) { this.promotionId = promotionId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
      public List<OrderPromotion> getOrderPromotions() { return orderPromotions; }
    public void setOrderPromotions(List<OrderPromotion> orderPromotions) { this.orderPromotions = orderPromotions; }
    
    public BigDecimal getMinimumPurchaseAmount() { return minimumPurchaseAmount; }
    public void setMinimumPurchaseAmount(BigDecimal minimumPurchaseAmount) { this.minimumPurchaseAmount = minimumPurchaseAmount; }
    
    public Integer getMaximumUses() { return maximumUses; }
    public void setMaximumUses(Integer maximumUses) { this.maximumUses = maximumUses; }
    
    public Integer getCurrentUses() { return currentUses; }
    public void setCurrentUses(Integer currentUses) { this.currentUses = currentUses; }
    
    public String getPromotionType() { return promotionType; }
    public void setPromotionType(String promotionType) { this.promotionType = promotionType; }
    
    public BigDecimal getMaxDiscountAmount() { return maxDiscountAmount; }
    public void setMaxDiscountAmount(BigDecimal maxDiscountAmount) { this.maxDiscountAmount = maxDiscountAmount; }
}
