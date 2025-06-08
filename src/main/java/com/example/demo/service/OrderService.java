package com.example.demo.service;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderDetail;
import com.example.demo.entity.OrderStatus;
import com.example.demo.entity.Payment;
import com.example.demo.entity.PaymentType;
import com.example.demo.entity.PaymentStatus;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.OrderDetailRepository;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    
    // Add promotion and Midtrans services
    @Autowired
    private PromotionService promotionService;
    
    @Autowired
    private MidtransService midtransService;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }
    
    /**
     * Get payment info for all orders
     */
    public List<Map<String, Object>> getOrdersWithPaymentInfo() {
        List<Order> orders = orderRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Order order : orders) {
            Map<String, Object> orderInfo = new HashMap<>();
            orderInfo.put("orderId", order.getOrderId());
            orderInfo.put("status", order.getStatus());
            orderInfo.put("date", order.getOrderDate());
            orderInfo.put("userName", order.getUser().getUsername());
            
            List<Payment> payments = paymentRepository.findByOrderOrderId(order.getOrderId());
            orderInfo.put("paymentCount", payments.size());
            
            if (!payments.isEmpty()) {
                orderInfo.put("paymentStatus", payments.get(0).getStatus());
                orderInfo.put("paymentAmount", payments.get(0).getAmount());
            } else {
                orderInfo.put("paymentStatus", "NO_PAYMENT");
                orderInfo.put("paymentAmount", BigDecimal.ZERO);
            }
            
            result.add(orderInfo);
        }
        
        return result;
    }
      @Transactional
    public Order createOrder(String userId, List<OrderItemRequest> items) {
        // Find the user
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        // Create the order
        Order order = new Order(user, OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());
        
        // Save the order first to get the ID
        Order savedOrder = orderRepository.save(order);
        
        // Create order details
        List<OrderDetail> orderDetails = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (OrderItemRequest item : items) {
            Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + item.getProductId()));
              if (!product.getIsAvailable()) {
                throw new RuntimeException("Product is not available: " + product.getName());
            }
            
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
            
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(savedOrder);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(item.getQuantity());
            orderDetail.setUnitPrice(product.getPrice());
            
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
            
            orderDetails.add(orderDetail);
        }
        
        // Save order details
        orderDetailRepository.saveAll(orderDetails);
        
        // Set the order details to the order
        savedOrder.setOrderDetails(orderDetails);
          return savedOrder;
    }
      @Transactional
    public Order createOrderWithPayment(String userId, List<OrderItemRequest> items, 
                                       com.example.demo.controller.OrderController.PaymentInfo paymentInfo) {
        // Create the order first
        Order order = createOrder(userId, items);
        
        // Calculate total amount from order details
        BigDecimal totalAmount = order.getOrderDetails().stream()
            .map(detail -> detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
          // Create payment record
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(totalAmount);
        payment.setPaymentDate(LocalDateTime.now());
        
        // Set payment type using the enum's fromString method
        try {
            PaymentType paymentType = PaymentType.fromString(paymentInfo.getType());
            payment.setType(paymentType);
            payment.setStatus(PaymentStatus.PENDING);
            
            // Set payment method based on type
            String paymentMethod = paymentInfo.getPaymentMethod();
            if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
                // Set default payment method based on type
                switch (paymentType) {
                    case CASH:
                        paymentMethod = "cash";
                        break;
                    case CARD:
                    case CREDIT_CARD:
                    case DEBIT_CARD:
                        paymentMethod = "card";
                        break;
                    case DIGITAL:
                    case E_WALLET:
                        paymentMethod = "digital_wallet";
                        break;
                    case BANK_TRANSFER:
                        paymentMethod = "bank_transfer";
                        break;                    case VIRTUAL_ACCOUNT:
                        paymentMethod = "virtual_account";
                        break;
                    case QRIS:
                        paymentMethod = "qris";
                        break;
                    default:
                        paymentMethod = paymentType.getValue();
                        break;
                }
            }
            payment.setPaymentMethod(paymentMethod);
            
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid payment type: " + paymentInfo.getType());
        }
          // Set transaction ID if provided (for Midtrans payments)
        if (paymentInfo.getTransactionId() != null) {
            payment.setTransactionId(paymentInfo.getTransactionId());
        }
        
        // Set additional payment fields if provided
        if (paymentInfo.getBank() != null) {
            payment.setBank(paymentInfo.getBank());
        }
        if (paymentInfo.getVaNumber() != null) {
            payment.setVaNumber(paymentInfo.getVaNumber());
        }
        if (paymentInfo.getThreeDs() != null) {
            payment.setThreeDs(paymentInfo.getThreeDs());
        }
        
        // Save payment
        Payment savedPayment = paymentRepository.save(payment);
        
        // Add the payment to the order's payments list
        if (order.getPayments() == null) {
            order.setPayments(new ArrayList<>());
        }
        order.getPayments().add(savedPayment);
        
        return order;
    }
    
    /**
     * Enhanced method to create order with payment and promotion support
     */
    @Transactional
    public Order createOrderWithPaymentAndPromotions(String userId, List<OrderItemRequest> items, 
                                                   com.example.demo.controller.OrderController.PaymentInfo paymentInfo,
                                                   List<Long> promotionIds) {
        // Create the order first
        Order order = createOrder(userId, items);
        
        // Calculate original total amount from order details
        BigDecimal originalAmount = order.getOrderDetails().stream()
            .map(detail -> detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Apply promotions and calculate final amount
        PromotionService.CalculationResult promotionResult = 
            promotionService.calculateFinalAmount(originalAmount, promotionIds);
        
        // Apply promotions to order (create OrderPromotion records)
        promotionService.applyPromotionsToOrder(order, promotionIds);
        
        // Create payment record with final amount
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(promotionResult.getFinalAmount()); // Use discounted amount
        payment.setPaymentDate(LocalDateTime.now());
        
        // Set original amount in grossAmount field for reference
        payment.setGrossAmount(originalAmount);
        
        // Set payment type using the enum's fromString method
        try {
            PaymentType paymentType = PaymentType.fromString(paymentInfo.getType());
            payment.setType(paymentType);
            payment.setStatus(PaymentStatus.PENDING);
            
            // Set payment method based on type
            String paymentMethod = paymentInfo.getPaymentMethod();
            if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
                // Set default payment method based on type
                switch (paymentType) {
                    case CASH:
                        paymentMethod = "cash";
                        break;
                    case CARD:
                    case CREDIT_CARD:
                    case DEBIT_CARD:
                        paymentMethod = "card";
                        break;
                    case DIGITAL:
                    case E_WALLET:
                        paymentMethod = "digital_wallet";
                        break;
                    case BANK_TRANSFER:
                        paymentMethod = "bank_transfer";
                        break;                    case VIRTUAL_ACCOUNT:
                        paymentMethod = "virtual_account";
                        break;
                    case QRIS:
                        paymentMethod = "qris";
                        break;
                    default:
                        paymentMethod = paymentType.getValue();
                        break;
                }
            }
            payment.setPaymentMethod(paymentMethod);
              // For Midtrans payments, prepare additional data
            if ("credit_card".equals(paymentMethod) || "bank_transfer".equals(paymentMethod) || 
                "digital_wallet".equals(paymentMethod) || "virtual_account".equals(paymentMethod) || 
                "qris".equals(paymentMethod)) {
                
                // Set Midtrans order ID
                payment.setMidtransOrderId("ORDER-" + order.getOrderId());
                
                // Store promotion metadata in custom fields (can be retrieved later)
                if (promotionResult.getTotalDiscount().compareTo(BigDecimal.ZERO) > 0) {
                    payment.setPaymentReference("DISCOUNT_APPLIED:" + promotionResult.getTotalDiscount().toString());
                }
            }
            
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid payment type: " + paymentInfo.getType());
        }
        
        // Set transaction ID if provided (for Midtrans payments)
        if (paymentInfo.getTransactionId() != null) {
            payment.setTransactionId(paymentInfo.getTransactionId());
        }
        
        // Set additional payment fields if provided
        if (paymentInfo.getBank() != null) {
            payment.setBank(paymentInfo.getBank());
        }
        if (paymentInfo.getVaNumber() != null) {
            payment.setVaNumber(paymentInfo.getVaNumber());
        }
        if (paymentInfo.getThreeDs() != null) {
            payment.setThreeDs(paymentInfo.getThreeDs());
        }
        
        // Save payment
        Payment savedPayment = paymentRepository.save(payment);
        
        // Add the payment to the order's payments list
        if (order.getPayments() == null) {
            order.setPayments(new ArrayList<>());
        }
        order.getPayments().add(savedPayment);
        
        return order;
    }
    
    @Transactional
    public void updatePaymentStatus(Long orderId, 
                                   com.example.demo.controller.OrderController.PaymentUpdateRequest request) {
        // Find the order
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        
        // Find the payment by transaction ID or order ID
        List<Payment> payments = paymentRepository.findByOrderOrderId(orderId);
        Payment payment = null;
        
        if (request.getTransactionId() != null) {
            // Find by transaction ID first
            payment = payments.stream()
                .filter(p -> request.getTransactionId().equals(p.getTransactionId()))
                .findFirst()
                .orElse(null);
        }
        
        if (payment == null && !payments.isEmpty()) {
            // Fallback to latest payment for this order
            payment = payments.get(payments.size() - 1);
        }
        
        if (payment == null) {
            throw new RuntimeException("Payment not found for order: " + orderId);
        }
          // Update payment status based on Midtrans response
        switch (request.getStatus().toLowerCase()) {
            case "settlement":
            case "capture":
                payment.setStatus(PaymentStatus.COMPLETED);
                order.setStatus(OrderStatus.PREPARED);
                break;
            case "pending":
                payment.setStatus(PaymentStatus.PENDING);
                break;
            case "deny":
            case "cancel":
            case "expire":
            case "failure":
                payment.setStatus(PaymentStatus.FAILED);
                order.setStatus(OrderStatus.CANCELLED);
                break;
            default:
                throw new RuntimeException("Unknown payment status: " + request.getStatus());
        }
        
        // Update additional payment information
        if (request.getFraudStatus() != null) {
            payment.setFraudStatus(request.getFraudStatus());
        }
        if (request.getBank() != null) {
            payment.setBank(request.getBank());
        }
        if (request.getVaNumber() != null) {
            payment.setVaNumber(request.getVaNumber());
        }
        
        // Save updated payment and order
        paymentRepository.save(payment);
        orderRepository.save(order);
    }
    
    // Inner class for order item requests
    public static class OrderItemRequest {
        private Long productId;
        private Integer quantity;
        
        public OrderItemRequest() {}
        
        public OrderItemRequest(Long productId, Integer quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }
        
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}
