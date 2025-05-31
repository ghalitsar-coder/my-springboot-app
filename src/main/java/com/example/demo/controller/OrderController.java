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
     * Create a new order with payment information
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request) {
        try {
            Order order = orderService.createOrderWithPayment(
                request.getUserId(), 
                request.getItems(),
                request.getPaymentInfo()
            );
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Update payment status after Midtrans callback
     */
    @PostMapping("/{orderId}/payment")
    public ResponseEntity<?> updatePaymentStatus(
            @PathVariable Long orderId,
            @RequestBody PaymentUpdateRequest request) {
        try {
            orderService.updatePaymentStatus(orderId, request);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
      /**
     * Request object for creating orders with payment information
     */
    public static class CreateOrderRequest {
        private Long userId;
        private List<OrderItemRequest> items;
        private PaymentInfo paymentInfo;
        
        public CreateOrderRequest() {}
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public List<OrderItemRequest> getItems() { return items; }
        public void setItems(List<OrderItemRequest> items) { this.items = items; }
        
        public PaymentInfo getPaymentInfo() { return paymentInfo; }
        public void setPaymentInfo(PaymentInfo paymentInfo) { this.paymentInfo = paymentInfo; }
    }
    
    /**
     * Payment information for order creation
     */
    public static class PaymentInfo {
        private String type; // "cash", "card", "digital"
        private String transactionId;
        private String paymentMethod;
        
        public PaymentInfo() {}
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
        
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    }
    
    /**
     * Request object for updating payment status
     */
    public static class PaymentUpdateRequest {
        private String transactionId;
        private String status;
        private String fraudStatus;
        private String bank;
        private String vaNumber;
        
        public PaymentUpdateRequest() {}
        
        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getFraudStatus() { return fraudStatus; }
        public void setFraudStatus(String fraudStatus) { this.fraudStatus = fraudStatus; }
        
        public String getBank() { return bank; }
        public void setBank(String bank) { this.bank = bank; }
        
        public String getVaNumber() { return vaNumber; }
        public void setVaNumber(String vaNumber) { this.vaNumber = vaNumber; }
    }
}
