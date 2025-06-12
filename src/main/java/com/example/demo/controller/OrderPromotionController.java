package com.example.demo.controller;

import com.example.demo.dto.ErrorResponseDTO;
import com.example.demo.entity.OrderPromotion;
import com.example.demo.entity.Order;
import com.example.demo.entity.Promotion;
import com.example.demo.repository.OrderPromotionRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/order-promotions")
public class OrderPromotionController {

    @Autowired
    private OrderPromotionRepository orderPromotionRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PromotionRepository promotionRepository;

    /**
     * Get all order-promotion relationships
     */
    @GetMapping
    public List<OrderPromotion> getAllOrderPromotions() {
        return orderPromotionRepository.findAll();
    }

    /**
     * Get order-promotion relationship by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderPromotion> getOrderPromotionById(@PathVariable Long id) {
        Optional<OrderPromotion> orderPromotion = orderPromotionRepository.findById(id);
        return orderPromotion.map(op -> ResponseEntity.ok().body(op))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all promotions applied to a specific order
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getPromotionsByOrderId(@PathVariable Long orderId) {
        try {
            Optional<Order> order = orderRepository.findById(orderId);
            if (order.isEmpty()) {
                ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    "Order not found with id: " + orderId,
                    "/api/order-promotions/order/" + orderId
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            List<OrderPromotion> orderPromotions = order.get().getOrderPromotions();
            return ResponseEntity.ok(orderPromotions);
        } catch (Exception e) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                e.getMessage(),
                "/api/order-promotions/order/" + orderId
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get all orders that use a specific promotion
     */
    @GetMapping("/promotion/{promotionId}")
    public ResponseEntity<?> getOrdersByPromotionId(@PathVariable Long promotionId) {
        try {
            Optional<Promotion> promotion = promotionRepository.findById(promotionId);
            if (promotion.isEmpty()) {
                ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    "Promotion not found with id: " + promotionId,
                    "/api/order-promotions/promotion/" + promotionId
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            List<OrderPromotion> orderPromotions = promotion.get().getOrderPromotions();
            return ResponseEntity.ok(orderPromotions);
        } catch (Exception e) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                e.getMessage(),
                "/api/order-promotions/promotion/" + promotionId
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Apply a promotion to an order
     */
    @PostMapping
    public ResponseEntity<?> applyPromotionToOrder(@RequestBody Map<String, Object> request) {
        try {
            Long orderId = Long.valueOf(request.get("orderId").toString());
            Long promotionId = Long.valueOf(request.get("promotionId").toString());

            // Validate order exists
            Optional<Order> order = orderRepository.findById(orderId);
            if (order.isEmpty()) {
                ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    "Order not found with id: " + orderId,
                    "/api/order-promotions"
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            // Validate promotion exists
            Optional<Promotion> promotion = promotionRepository.findById(promotionId);
            if (promotion.isEmpty()) {
                ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    "Promotion not found with id: " + promotionId,
                    "/api/order-promotions"
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            // Check if promotion is active
            if (!promotion.get().getIsActive()) {
                ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    "Promotion is not active",
                    "/api/order-promotions"
                );
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Create the order-promotion relationship
            OrderPromotion orderPromotion = new OrderPromotion(order.get(), promotion.get());
            OrderPromotion savedOrderPromotion = orderPromotionRepository.save(orderPromotion);

            return ResponseEntity.ok(savedOrderPromotion);

        } catch (NumberFormatException e) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Invalid order ID or promotion ID format",
                "/api/order-promotions"
            );
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                e.getMessage(),
                "/api/order-promotions"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Remove a promotion from an order
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removePromotionFromOrder(@PathVariable Long id) {
        try {
            Optional<OrderPromotion> orderPromotion = orderPromotionRepository.findById(id);
            if (orderPromotion.isEmpty()) {
                ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    "Order-Promotion relationship not found with id: " + id,
                    "/api/order-promotions/" + id
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            orderPromotionRepository.deleteById(id);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                e.getMessage(),
                "/api/order-promotions/" + id
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
