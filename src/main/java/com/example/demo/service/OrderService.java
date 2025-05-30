package com.example.demo.service;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderDetail;
import com.example.demo.entity.OrderStatus;
import com.example.demo.entity.Payment;
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
    public Order createOrder(Long userId, List<OrderItemRequest> items) {
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
