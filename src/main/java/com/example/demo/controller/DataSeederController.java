package com.example.demo.controller;

import com.example.demo.config.ComprehensiveDataSeeder;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for manually triggering data seeding
 */
@RestController
@RequestMapping("/api/admin/seeder")
public class DataSeederController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CustomizationRepository customizationRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private PromotionRepository promotionRepository;
    
    @Autowired
    private OrderPromotionRepository orderPromotionRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private OrderCustomizationRepository orderCustomizationRepository;
    
    @Autowired
    private ComprehensiveDataSeeder dataSeeder;

    /**
     * Force data seeding without checking if data already exists
     */
    @PostMapping("/force")
    public ResponseEntity<String> forceDataSeeding() {
        try {
            System.out.println("Manual force seeding triggered");
            
            // Get existing user count to preserve
            long userCount = userRepository.count();
            
            // Use internal methods from the data seeder
            List<User> users = userRepository.findAll();
            List<Category> categories = dataSeeder.seedCategoriesManually();
            List<Product> products = dataSeeder.seedProductsManually(categories);
            List<Customization> customizations = dataSeeder.seedCustomizationsManually();
            List<Promotion> promotions = dataSeeder.seedPromotionsManually();
            List<Order> orders = dataSeeder.seedOrdersManually(users);
            dataSeeder.seedOrderDetailsManually(orders, products, customizations);
            dataSeeder.seedPaymentsManually(orders);
            dataSeeder.seedOrderPromotionsManually(orders, promotions);
            dataSeeder.seedReviewsManually(orders);
            
            return ResponseEntity.ok("Data seeding completed successfully. Added data while preserving " + userCount + " existing users.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error during force data seeding: " + e.getMessage());
        }
    }
    
    /**
     * Check current data status
     */
    @PostMapping("/status")
    public ResponseEntity<String> checkDataStatus() {
        try {
            long userCount = userRepository.count();
            long categoryCount = categoryRepository.count();
            long productCount = productRepository.count();
            long customizationCount = customizationRepository.count();
            long orderCount = orderRepository.count();
            long paymentCount = paymentRepository.count();
            
            String status = "Current Database Status:\n" +
                    "Users: " + userCount + "\n" +
                    "Categories: " + categoryCount + "\n" +
                    "Products: " + productCount + "\n" +
                    "Customizations: " + customizationCount + "\n" +
                    "Orders: " + orderCount + "\n" +
                    "Payments: " + paymentCount;
            
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error checking data status: " + e.getMessage());
        }
    }
}
