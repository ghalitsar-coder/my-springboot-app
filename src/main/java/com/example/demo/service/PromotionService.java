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
    private OrderPromotionRepository orderPromotionRepository;

    /**
     * Get all active promotions
     */
    public List<Promotion> getActivePromotions() {
        LocalDate today = LocalDate.now();
        return promotionRepository.findAll().stream()
                .filter(promotion -> promotion.getIsActive() &&
                        promotion.getStartDate().compareTo(today) <= 0 &&
                        promotion.getEndDate().compareTo(today) >= 0)
                .collect(Collectors.toList());
    }    /**
     * Calculate discount amount for a given order total and promotion
     */
    public BigDecimal calculateDiscount(BigDecimal orderTotal, Promotion promotion) {
        if (promotion == null || !isPromotionValid(promotion)) {
            return BigDecimal.ZERO;
        }

        // Handle both decimal (0.15 for 15%) and percentage (15 for 15%) formats
        BigDecimal discountValue = promotion.getDiscountValue();
        
        // If discount value is greater than 1, treat it as percentage (e.g., 15 = 15%)
        // If discount value is less than or equal to 1, treat it as decimal (e.g., 0.15 = 15%)
        if (discountValue.compareTo(BigDecimal.ONE) > 0) {
            // Percentage format: divide by 100
            return orderTotal.multiply(discountValue.divide(BigDecimal.valueOf(100)));
        } else {
            // Decimal format: use directly
            return orderTotal.multiply(discountValue);
        }
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
    }

    /**
     * Apply promotions to an order (create OrderPromotion records)
     */
    public void applyPromotionsToOrder(Order order, List<Long> promotionIds) {
        if (promotionIds != null && !promotionIds.isEmpty()) {
            List<Promotion> validPromotions = promotionRepository.findAllById(promotionIds)
                    .stream()
                    .filter(this::isPromotionValid)
                    .collect(Collectors.toList());
            
            for (Promotion promotion : validPromotions) {
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
