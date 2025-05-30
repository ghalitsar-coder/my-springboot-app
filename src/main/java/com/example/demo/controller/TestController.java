package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Controller for testing API endpoints and JSON serialization
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    
    /**
     * Test endpoint for product serialization
     */
    @GetMapping("/product/{id}")
    public ResponseEntity<Map<String, Object>> testProductSerialization(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            response.put("product", product);
            
            // Test category relationship
            Category category = product.getCategory();
            if (category != null) {
                response.put("categoryName", category.getName());
                response.put("categoryProductsCount", category.getProducts() != null ? 
                                                    category.getProducts().size() : 0);
            }
            
            // Test order details relationship
            List<OrderDetail> details = product.getOrderDetails();
            if (details != null) {
                response.put("orderDetailsCount", details.size());
                if (!details.isEmpty()) {
                    OrderDetail firstDetail = details.get(0);
                    if (firstDetail.getOrder() != null) {
                        response.put("orderIdFromDetail", firstDetail.getOrder().getOrderId());
                    }
                }
            }
            
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Test endpoint for order serialization
     */
    @GetMapping("/order/{id}")
    public ResponseEntity<Map<String, Object>> testOrderSerialization(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            response.put("order", order);
            
            // Test user relationship
            User user = order.getUser();
            if (user != null) {
                response.put("userName", user.getUsername());
                response.put("userOrdersCount", user.getOrders() != null ? 
                                              user.getOrders().size() : 0);
            }
            
            // Test order details relationship
            List<OrderDetail> details = order.getOrderDetails();
            if (details != null) {
                response.put("orderDetailsCount", details.size());
                
                if (!details.isEmpty()) {
                    OrderDetail firstDetail = details.get(0);
                    if (firstDetail.getProduct() != null) {
                        response.put("productFromDetail", firstDetail.getProduct().getName());
                    }
                    
                    List<OrderCustomization> customizations = firstDetail.getOrderCustomizations();
                    if (customizations != null && !customizations.isEmpty()) {
                        response.put("customizationsCount", customizations.size());
                    }
                }
            }
            
            // Test payments relationship
            List<Payment> payments = order.getPayments();
            if (payments != null) {
                response.put("paymentsCount", payments.size());
                if (!payments.isEmpty()) {
                    Payment firstPayment = payments.get(0);
                    response.put("paymentStatus", firstPayment.getStatus());
                    response.put("paymentAmount", firstPayment.getAmount());
                }
            }
            
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Test endpoint for database schema
     */
    @GetMapping("/db-schema")
    public ResponseEntity<Map<String, Object>> testDatabaseSchema() {
        Map<String, Object> response = new HashMap<>();
        
        // Count records in each table
        response.put("productsCount", productRepository.count());
        response.put("categoriesCount", categoryRepository.count());
        response.put("ordersCount", orderRepository.count());
        response.put("orderDetailsCount", orderDetailRepository.count());
        response.put("paymentsCount", paymentRepository.count());
        
        // Check if schema is set correctly
        try {
            List<Object[]> schemaInfo = productRepository.getSchemaInfo();
            List<Map<String, String>> schemas = new ArrayList<>();
            
            for (Object[] row : schemaInfo) {
                Map<String, String> schemaRow = new HashMap<>();
                schemaRow.put("tableName", (String) row[0]);
                schemaRow.put("schemaName", (String) row[1]);
                schemas.add(schemaRow);
            }
            
            response.put("schemas", schemas);
            response.put("schemaCheckSuccess", true);
        } catch (Exception e) {
            response.put("schemaCheckSuccess", false);
            response.put("schemaCheckError", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}
