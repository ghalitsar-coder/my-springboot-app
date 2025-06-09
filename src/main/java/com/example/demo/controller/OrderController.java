package com.example.demo.controller;

import com.example.demo.entity.Order;
import com.example.demo.service.OrderService;
import com.example.demo.service.PromotionService;
import com.example.demo.service.OrderService.OrderItemRequest;
import com.example.demo.dto.ErrorResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
      @Autowired
    private PromotionService promotionService;
    
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
    }    /**
     * Create a new order with payment information
     */
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest request) {
        try {
            Order order = orderService.createOrderWithPayment(
                request.getUserId(), 
                request.getItems(),
                request.getPaymentInfo()
            );
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                e.getMessage(),
                "/api/orders"
            );
            return ResponseEntity.badRequest().body(errorResponse);
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
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                e.getMessage(),
                "/api/orders/" + orderId + "/payment"
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
      /**
     * Create a new order with payment and promotion support
     */
    @PostMapping("/with-promotions")
    public ResponseEntity<?> createOrderWithPromotions(@RequestBody CreateOrderWithPromotionsRequest request) {
        try {
            Order order = orderService.createOrderWithPaymentAndPromotions(
                request.getUserId(), 
                request.getItems(), 
                request.getPaymentInfo(),
                request.getPromotionIds()
            );
            
            // Return the order with payment information
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                e.getMessage(),
                "/api/orders/with-promotions"
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }    /**
     * Get available promotions for order creation
     */
    @GetMapping("/available-promotions")
    public ResponseEntity<?> getAvailablePromotions(@RequestParam(required = false) BigDecimal orderTotal) {
        try {
            List<com.example.demo.entity.Promotion> promotions;
            
            if (orderTotal != null) {
                // Get eligible promotions based on order total
                promotions = promotionService.getEligiblePromotions(orderTotal);
            } else {
                // Get all active promotions
                promotions = promotionService.getActivePromotions();
            }
            
            return ResponseEntity.ok(promotions);
        } catch (Exception e) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Failed to fetch available promotions: " + e.getMessage(),
                "/api/orders/available-promotions"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Request object for creating orders with payment information
     */    public static class CreateOrderRequest {
        private String userId;
        private List<OrderItemRequest> items;
        private PaymentInfo paymentInfo;
        
        public CreateOrderRequest() {}
        
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        
        public List<OrderItemRequest> getItems() { return items; }
        public void setItems(List<OrderItemRequest> items) { this.items = items; }
        
        public PaymentInfo getPaymentInfo() { return paymentInfo; }
        public void setPaymentInfo(PaymentInfo paymentInfo) { this.paymentInfo = paymentInfo; }
    }
      /**
     * Payment information for order creation
     */
    public static class PaymentInfo {
        private String type; // "cash", "card", "digital", "credit_card", "bank_transfer", etc.
        private String transactionId;
        private String paymentMethod;
        private String bank;
        private String vaNumber;
        private Boolean threeDs;
        
        public PaymentInfo() {}
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
        
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
        
        public String getBank() { return bank; }
        public void setBank(String bank) { this.bank = bank; }
        
        public String getVaNumber() { return vaNumber; }
        public void setVaNumber(String vaNumber) { this.vaNumber = vaNumber; }
        
        public Boolean getThreeDs() { return threeDs; }
        public void setThreeDs(Boolean threeDs) { this.threeDs = threeDs; }
    }
      /**
     * Request object for updating payment status (Midtrans webhook)
     */
    public static class PaymentUpdateRequest {
        private String transactionId;
        private String status;
        private String fraudStatus;
        private String bank;
        private String vaNumber;
        
        // Additional Midtrans fields
        private String orderId;
        private String grossAmount;
        private String paymentType;
        private String statusCode;
        private String statusMessage;
        private String signatureKey;
        private String settlementTime;
        private String transactionTime;
        private String transactionStatus;
        private String paymentChannel;
        private String merchantId;
        
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
        
        // Midtrans-specific getters and setters
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        
        public String getGrossAmount() { return grossAmount; }
        public void setGrossAmount(String grossAmount) { this.grossAmount = grossAmount; }
        
        public String getPaymentType() { return paymentType; }
        public void setPaymentType(String paymentType) { this.paymentType = paymentType; }
        
        public String getStatusCode() { return statusCode; }
        public void setStatusCode(String statusCode) { this.statusCode = statusCode; }
        
        public String getStatusMessage() { return statusMessage; }
        public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }
        
        public String getSignatureKey() { return signatureKey; }
        public void setSignatureKey(String signatureKey) { this.signatureKey = signatureKey; }
        
        public String getSettlementTime() { return settlementTime; }
        public void setSettlementTime(String settlementTime) { this.settlementTime = settlementTime; }
        
        public String getTransactionTime() { return transactionTime; }
        public void setTransactionTime(String transactionTime) { this.transactionTime = transactionTime; }
        
        public String getTransactionStatus() { return transactionStatus; }
        public void setTransactionStatus(String transactionStatus) { this.transactionStatus = transactionStatus; }
        
        public String getPaymentChannel() { return paymentChannel; }
        public void setPaymentChannel(String paymentChannel) { this.paymentChannel = paymentChannel; }
        
        public String getMerchantId() { return merchantId; }
        public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
    }
    
    /**
     * Dedicated Midtrans webhook endpoint
     */
    @PostMapping("/midtrans/notification")
    public ResponseEntity<?> handleMidtransNotification(@RequestBody PaymentUpdateRequest request) {
        try {
            // TODO: Add signature verification for security
            // String serverKey = "your-server-key";
            // String signatureKey = DigestUtils.sha512Hex(request.getOrderId() + request.getStatusCode() + request.getGrossAmount() + serverKey);
            // if (!signatureKey.equals(request.getSignatureKey())) {
            //     throw new RuntimeException("Invalid signature");
            // }
            
            // Extract order ID from Midtrans order_id (if it contains your order ID)
            Long orderId = extractOrderIdFromMidtransOrderId(request.getOrderId());
            
            orderService.updatePaymentStatus(orderId, request);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Payment status updated successfully");
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                e.getMessage(),
                "/api/orders/midtrans/notification"
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Extract order ID from Midtrans order_id
     * Customize this method based on your order ID format
     */
    private Long extractOrderIdFromMidtransOrderId(String midtransOrderId) {
        if (midtransOrderId == null) {
            throw new RuntimeException("Midtrans order ID is required");
        }
        
        // Example: if your Midtrans order_id format is "ORDER-{orderId}-{timestamp}"
        // You would extract the orderId part here
        try {
            // Simple case: if Midtrans order_id is just the order ID
            return Long.parseLong(midtransOrderId);
        } catch (NumberFormatException e) {
            // If your format is more complex, implement the extraction logic here
            // For example: ORDER-123-20231204
            if (midtransOrderId.startsWith("ORDER-")) {
                String[] parts = midtransOrderId.split("-");
                if (parts.length >= 2) {
                    return Long.parseLong(parts[1]);
                }
            }
            throw new RuntimeException("Invalid Midtrans order ID format: " + midtransOrderId);
        }
    }
    
    /**
     * Request object for creating orders with payment and promotion information
     */    
    public static class CreateOrderWithPromotionsRequest {
        private String userId;
        private List<OrderItemRequest> items;
        private PaymentInfo paymentInfo;
        private List<Long> promotionIds;
        
        public CreateOrderWithPromotionsRequest() {}
        
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        
        public List<OrderItemRequest> getItems() { return items; }
        public void setItems(List<OrderItemRequest> items) { this.items = items; }
        
        public PaymentInfo getPaymentInfo() { return paymentInfo; }
        public void setPaymentInfo(PaymentInfo paymentInfo) { this.paymentInfo = paymentInfo; }
        
        public List<Long> getPromotionIds() { return promotionIds; }
        public void setPromotionIds(List<Long> promotionIds) { this.promotionIds = promotionIds; }
    }
      /**
     * Update order status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody OrderStatusUpdateRequest request) {        try {
            Order updatedOrder = orderService.updateOrderStatus(id, request.getStatus());
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                e.getMessage(),
                "/api/orders/" + id + "/status"
            );
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Request object for updating order status
     */
    public static class OrderStatusUpdateRequest {
        private String status;
        
        public OrderStatusUpdateRequest() {}
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
