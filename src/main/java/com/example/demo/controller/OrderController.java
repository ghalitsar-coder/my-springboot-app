package com.example.demo.controller;

import com.example.demo.entity.Order;
import com.example.demo.service.OrderService;
import com.example.demo.service.OrderService.OrderItemRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
            .map(order -> ResponseEntity.ok().body(order))
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get order payment info
     */
    @GetMapping("/{id}/payment")
    public ResponseEntity<?> getOrderPayments(@PathVariable Long id) {
        return orderService.getOrderById(id)
            .map(order -> ResponseEntity.ok(order.getPayments()))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    /**
     * Get payment summary for all orders
     */
    @GetMapping("/payment-summary")
    public ResponseEntity<?> getOrdersPaymentSummary() {
        return ResponseEntity.ok(orderService.getOrdersWithPaymentInfo());
    }
    
    /**
     * Create a new order
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request) {
        try {
            Order order = orderService.createOrder(request.getUserId(), request.getItems());
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Request object for creating orders
     */
    public static class CreateOrderRequest {
        private Long userId;
        private List<OrderItemRequest> items;
        
        public CreateOrderRequest() {}
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public List<OrderItemRequest> getItems() { return items; }
        public void setItems(List<OrderItemRequest> items) { this.items = items; }
    }
}
