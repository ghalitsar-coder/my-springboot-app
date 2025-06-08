package com.example.demo.service;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderPromotion;
import com.example.demo.entity.Promotion;
import com.example.demo.repository.OrderPromotionRepository;
import com.example.demo.repository.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private OrderPromotionRepository orderPromotionRepository;    /**
     * Get all active promotions
     */
    public List<Promotion> getActivePromotions() {
        LocalDate today = LocalDate.now();
        return promotionRepository.findAll().stream()
                .filter(promotion -> promotion.getIsActive() &&
                        promotion.getStartDate().compareTo(today) <= 0 &&
                        promotion.getEndDate().compareTo(today) >= 0)
                .collect(Collectors.toList());
    }
    
    /**
     * Get eligible promotions for a given order total
     */
    public List<Promotion> getEligiblePromotions(BigDecimal orderTotal) {
        return getActivePromotions().stream()
                .filter(promotion -> isPromotionEligible(promotion, orderTotal))
                .collect(Collectors.toList());
    }
    
    /**
     * Check if a promotion is eligible for the given order total
     */
    public boolean isPromotionEligible(Promotion promotion, BigDecimal orderTotal) {
        // Check if promotion is valid (active and within date range)
        if (!isPromotionValid(promotion)) {
            return false;
        }
        
        // Check minimum purchase amount
        if (promotion.getMinimumPurchaseAmount() != null && 
            orderTotal.compareTo(promotion.getMinimumPurchaseAmount()) < 0) {
            return false;
        }
        
        // Check maximum uses
        if (promotion.getMaximumUses() != null && 
            promotion.getCurrentUses() != null &&
            promotion.getCurrentUses() >= promotion.getMaximumUses()) {
            return false;
        }
        
        return true;
    }    /**
     * Calculate discount amount for a given order total and promotion
     */
    public BigDecimal calculateDiscount(BigDecimal orderTotal, Promotion promotion) {
        if (promotion == null || !isPromotionValid(promotion)) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount = BigDecimal.ZERO;
        String promotionType = promotion.getPromotionType();
        BigDecimal discountValue = promotion.getDiscountValue();
        
        if ("FIXED_AMOUNT".equals(promotionType)) {
            // Fixed amount discount
            discount = discountValue;
        } else {
            // Percentage discount (default)
            // Handle both decimal (0.15 for 15%) and percentage (15 for 15%) formats
            if (discountValue.compareTo(BigDecimal.ONE) > 0) {
                // Percentage format: divide by 100
                discount = orderTotal.multiply(discountValue.divide(BigDecimal.valueOf(100)));
            } else {
                // Decimal format: use directly
                discount = orderTotal.multiply(discountValue);
            }
        }
        
        // Apply maximum discount amount limit if specified
        if (promotion.getMaxDiscountAmount() != null && 
            discount.compareTo(promotion.getMaxDiscountAmount()) > 0) {
            discount = promotion.getMaxDiscountAmount();
        }
        
        // Ensure discount doesn't exceed order total
        if (discount.compareTo(orderTotal) > 0) {
            discount = orderTotal;
        }
        
        return discount;
    }

    /**
     * Calculate final amount after applying promotions
     */
    public CalculationResult calculateFinalAmount(BigDecimal originalAmount, List<Long> promotionIds) {
        BigDecimal totalDiscount = BigDecimal.ZERO;
        
        if (promotionIds != null && !promotionIds.isEmpty()) {
            List<Promotion> validPromotions = promotionRepository.findAllById(promotionIds)
                    .stream()
                    .filter(this::isPromotionValid)
                    .collect(Collectors.toList());
            
            // Calculate total discount (simple addition, can be made more complex)
            for (Promotion promotion : validPromotions) {
                BigDecimal discount = calculateDiscount(originalAmount, promotion);
                totalDiscount = totalDiscount.add(discount);
            }
        }
        
        BigDecimal finalAmount = originalAmount.subtract(totalDiscount);
        
        // Ensure final amount is not negative
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }
        
        return new CalculationResult(originalAmount, totalDiscount, finalAmount, promotionIds);
    }    /**
     * Apply promotions to an order (create OrderPromotion records)
     */
    public void applyPromotionsToOrder(Order order, List<Long> promotionIds) {
        if (promotionIds != null && !promotionIds.isEmpty()) {
            List<Promotion> validPromotions = promotionRepository.findAllById(promotionIds)
                    .stream()
                    .filter(this::isPromotionValid)
                    .collect(Collectors.toList());
            
            for (Promotion promotion : validPromotions) {
                // Increment usage count
                if (promotion.getCurrentUses() == null) {
                    promotion.setCurrentUses(1);
                } else {
                    promotion.setCurrentUses(promotion.getCurrentUses() + 1);
                }
                promotionRepository.save(promotion);
                
                // Create order-promotion relationship
                OrderPromotion orderPromotion = new OrderPromotion(order, promotion);
                orderPromotionRepository.save(orderPromotion);
            }
        }
    }

    /**
     * Check if a promotion is currently valid
     */
    private boolean isPromotionValid(Promotion promotion) {
        LocalDate today = LocalDate.now();
        return promotion.getIsActive() &&
                promotion.getStartDate().compareTo(today) <= 0 &&
                promotion.getEndDate().compareTo(today) >= 0;
    }

    /**
     * Result class for promotion calculations
     */
    public static class CalculationResult {
        private final BigDecimal originalAmount;
        private final BigDecimal totalDiscount;
        private final BigDecimal finalAmount;
        private final List<Long> appliedPromotionIds;

        public CalculationResult(BigDecimal originalAmount, BigDecimal totalDiscount, 
                               BigDecimal finalAmount, List<Long> appliedPromotionIds) {
            this.originalAmount = originalAmount;
            this.totalDiscount = totalDiscount;
            this.finalAmount = finalAmount;
            this.appliedPromotionIds = appliedPromotionIds;
        }

        // Getters
        public BigDecimal getOriginalAmount() { return originalAmount; }
        public BigDecimal getTotalDiscount() { return totalDiscount; }
        public BigDecimal getFinalAmount() { return finalAmount; }
        public List<Long> getAppliedPromotionIds() { return appliedPromotionIds; }
    }
}
