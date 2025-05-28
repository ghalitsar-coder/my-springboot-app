package com.example.demo.controller;

import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for resetting the database and forcing data seeding
 * This should only be used in development environments
 */
@RestController
@RequestMapping("/api/admin/database")
public class DatabaseResetController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private OrderPromotionRepository orderPromotionRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private OrderCustomizationRepository orderCustomizationRepository;
    
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private CustomizationRepository customizationRepository;
    
    @Autowired
    private PromotionRepository promotionRepository;

    /**
     * Reset the database by clearing all tables in the correct order
     * to respect foreign key constraints
     * @return ResponseEntity with status message
     */
    @PostMapping("/reset")
    public ResponseEntity<String> resetDatabase() {
        try {
            // Delete data in the correct order to respect foreign key constraints
            reviewRepository.deleteAllInBatch();
            orderPromotionRepository.deleteAllInBatch();
            paymentRepository.deleteAllInBatch();
            orderCustomizationRepository.deleteAllInBatch();
            orderDetailRepository.deleteAllInBatch();
            orderRepository.deleteAllInBatch();
            productRepository.deleteAllInBatch();
            categoryRepository.deleteAllInBatch();
            customizationRepository.deleteAllInBatch();
            promotionRepository.deleteAllInBatch();
            userRepository.deleteAllInBatch();
            
            return ResponseEntity.ok("Database reset successfully. Restart the application to trigger data seeding.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error resetting database: " + e.getMessage());
        }
    }
}
