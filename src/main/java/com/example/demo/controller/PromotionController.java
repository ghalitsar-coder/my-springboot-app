package com.example.demo.controller;

import com.example.demo.dto.ErrorResponseDTO;
import com.example.demo.entity.Promotion;
import com.example.demo.repository.PromotionRepository;
import com.example.demo.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/promotions")
public class PromotionController {

    @Autowired
    private PromotionRepository promotionRepository;
    
    @Autowired
    private PromotionService promotionService;

    /**
     * Create a new promotion
     */
    @PostMapping
    public ResponseEntity<?> createPromotion(@RequestBody Map<String, Object> promotionData) {
        try {
            Promotion promotion = new Promotion();
            promotion.setName((String) promotionData.get("name"));
            promotion.setDescription((String) promotionData.get("description"));
            
            // Handle discount value
            Object discountValue = promotionData.get("discountValue");
            if (discountValue instanceof Number) {
                promotion.setDiscountValue(BigDecimal.valueOf(((Number) discountValue).doubleValue()));
            } else if (discountValue instanceof String) {
                promotion.setDiscountValue(new BigDecimal((String) discountValue));
            }
            
            // Handle dates
            if (promotionData.get("startDate") != null) {
                promotion.setStartDate(LocalDate.parse((String) promotionData.get("startDate")));
            }
            if (promotionData.get("endDate") != null) {
                promotion.setEndDate(LocalDate.parse((String) promotionData.get("endDate")));
            }
              // Handle active status
            if (promotionData.get("isActive") != null) {
                promotion.setIsActive((Boolean) promotionData.get("isActive"));
            }
            
            // Handle new promotion rule fields
            if (promotionData.get("minimumPurchaseAmount") != null) {
                Object minAmount = promotionData.get("minimumPurchaseAmount");
                if (minAmount instanceof Number) {
                    promotion.setMinimumPurchaseAmount(BigDecimal.valueOf(((Number) minAmount).doubleValue()));
                } else if (minAmount instanceof String) {
                    promotion.setMinimumPurchaseAmount(new BigDecimal((String) minAmount));
                }
            }
            
            if (promotionData.get("maximumUses") != null) {
                promotion.setMaximumUses((Integer) promotionData.get("maximumUses"));
            }
            
            if (promotionData.get("promotionType") != null) {
                promotion.setPromotionType((String) promotionData.get("promotionType"));
            }
            
            if (promotionData.get("maxDiscountAmount") != null) {
                Object maxDiscount = promotionData.get("maxDiscountAmount");
                if (maxDiscount instanceof Number) {
                    promotion.setMaxDiscountAmount(BigDecimal.valueOf(((Number) maxDiscount).doubleValue()));
                } else if (maxDiscount instanceof String) {
                    promotion.setMaxDiscountAmount(new BigDecimal((String) maxDiscount));
                }
            }
            
            Promotion savedPromotion = promotionRepository.save(promotion);
            return ResponseEntity.ok(savedPromotion);
            
        } catch (Exception e) {
            ErrorResponseDTO error = new ErrorResponseDTO(400, "Bad Request",
                "Error creating promotion: " + e.getMessage(), 
                "/api/promotions");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get all promotions
     */
    @GetMapping
    public ResponseEntity<List<Promotion>> getAllPromotions() {
        List<Promotion> promotions = promotionRepository.findAll();
        return ResponseEntity.ok(promotions);
    }    /**
     * Get active promotions only
     */
    @GetMapping("/active")
    public ResponseEntity<List<Promotion>> getActivePromotions() {
        List<Promotion> activePromotions = promotionRepository.findAll()
            .stream()
            .filter(p -> p.getIsActive() && 
                        p.getStartDate().isBefore(LocalDate.now().plusDays(1)) &&
                        p.getEndDate().isAfter(LocalDate.now().minusDays(1)))
            .toList();
        return ResponseEntity.ok(activePromotions);
    }
    
    /**
     * Get eligible promotions for a given order total
     */
    @GetMapping("/eligible")
    public ResponseEntity<List<Promotion>> getEligiblePromotions(@RequestParam BigDecimal orderTotal) {
        try {
            List<Promotion> eligiblePromotions = promotionService.getEligiblePromotions(orderTotal);
            return ResponseEntity.ok(eligiblePromotions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get promotion by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPromotionById(@PathVariable Long id) {
        return promotionRepository.findById(id)
                .map(promotion -> ResponseEntity.ok().body(promotion))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update promotion
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePromotion(@PathVariable Long id, 
                                           @RequestBody Map<String, Object> promotionData) {
        try {
            return promotionRepository.findById(id)
                    .map(promotion -> {
                        if (promotionData.containsKey("name")) {
                            promotion.setName((String) promotionData.get("name"));
                        }
                        if (promotionData.containsKey("description")) {
                            promotion.setDescription((String) promotionData.get("description"));
                        }
                        if (promotionData.containsKey("discountValue")) {
                            Object discountValue = promotionData.get("discountValue");
                            if (discountValue instanceof Number) {
                                promotion.setDiscountValue(BigDecimal.valueOf(((Number) discountValue).doubleValue()));
                            } else if (discountValue instanceof String) {
                                promotion.setDiscountValue(new BigDecimal((String) discountValue));
                            }
                        }
                        if (promotionData.containsKey("startDate")) {
                            promotion.setStartDate(LocalDate.parse((String) promotionData.get("startDate")));
                        }
                        if (promotionData.containsKey("endDate")) {
                            promotion.setEndDate(LocalDate.parse((String) promotionData.get("endDate")));
                        }                        if (promotionData.containsKey("isActive")) {
                            promotion.setIsActive((Boolean) promotionData.get("isActive"));
                        }
                        
                        // Handle new promotion rule fields
                        if (promotionData.containsKey("minimumPurchaseAmount")) {
                            Object minAmount = promotionData.get("minimumPurchaseAmount");
                            if (minAmount instanceof Number) {
                                promotion.setMinimumPurchaseAmount(BigDecimal.valueOf(((Number) minAmount).doubleValue()));
                            } else if (minAmount instanceof String) {
                                promotion.setMinimumPurchaseAmount(new BigDecimal((String) minAmount));
                            }
                        }
                        
                        if (promotionData.containsKey("maximumUses")) {
                            promotion.setMaximumUses((Integer) promotionData.get("maximumUses"));
                        }
                        
                        if (promotionData.containsKey("promotionType")) {
                            promotion.setPromotionType((String) promotionData.get("promotionType"));
                        }
                        
                        if (promotionData.containsKey("maxDiscountAmount")) {
                            Object maxDiscount = promotionData.get("maxDiscountAmount");
                            if (maxDiscount instanceof Number) {
                                promotion.setMaxDiscountAmount(BigDecimal.valueOf(((Number) maxDiscount).doubleValue()));
                            } else if (maxDiscount instanceof String) {
                                promotion.setMaxDiscountAmount(new BigDecimal((String) maxDiscount));
                            }
                        }
                        
                        Promotion updatedPromotion = promotionRepository.save(promotion);
                        return ResponseEntity.ok(updatedPromotion);
                    })
                    .orElse(ResponseEntity.notFound().build());
                    
        } catch (Exception e) {
            ErrorResponseDTO error = new ErrorResponseDTO(400, "Bad Request",
                "Error updating promotion: " + e.getMessage(), 
                "/api/promotions/" + id);
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Delete promotion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePromotion(@PathVariable Long id) {
        try {
            return promotionRepository.findById(id)
                    .map(promotion -> {
                        // Check if promotion is used in any orders
                        if (promotion.getOrderPromotions() != null && !promotion.getOrderPromotions().isEmpty()) {
                            ErrorResponseDTO error = new ErrorResponseDTO(400, "Bad Request", 
                                "Cannot delete promotion because it has been used in " + promotion.getOrderPromotions().size() + " orders. Consider deactivating it instead.", 
                                "/api/promotions/" + id);
                            return ResponseEntity.badRequest().body(error);
                        }
                        
                        promotionRepository.delete(promotion);
                        return ResponseEntity.ok().build();
                    })
                    .orElse(ResponseEntity.notFound().build());
                    
        } catch (Exception e) {
            ErrorResponseDTO error = new ErrorResponseDTO(500, "Internal Server Error",
                "An error occurred while deleting the promotion: " + e.getMessage(), 
                "/api/promotions/" + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Activate/Deactivate promotion
     */
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> togglePromotionStatus(@PathVariable Long id) {
        try {
            return promotionRepository.findById(id)
                    .map(promotion -> {
                        promotion.setIsActive(!promotion.getIsActive());
                        Promotion updatedPromotion = promotionRepository.save(promotion);
                        return ResponseEntity.ok(updatedPromotion);
                    })
                    .orElse(ResponseEntity.notFound().build());
                    
        } catch (Exception e) {
            ErrorResponseDTO error = new ErrorResponseDTO(500, "Internal Server Error",
                "An error occurred while toggling promotion status: " + e.getMessage(), 
                "/api/promotions/" + id + "/toggle");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
