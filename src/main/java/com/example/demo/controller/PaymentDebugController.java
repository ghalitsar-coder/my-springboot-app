package com.example.demo.controller;

import com.example.demo.config.ComprehensiveDataSeeder;
import com.example.demo.entity.Order;
import com.example.demo.entity.Payment;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.PaymentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class PaymentDebugController {

    @Autowired
    private ComprehensiveDataSeeder dataSeeder;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @GetMapping("/reseed-payments")
    public ResponseEntity<?> reseedPayments() {
        // Delete all existing payments
        long beforeCount = paymentRepository.count();
        paymentRepository.deleteAll();
        
        // Get all orders
        List<Order> orders = orderRepository.findAll();
        
        // Reseed payments
        dataSeeder.seedPaymentsManually(orders);
        
        // Count payments after reseeding
        long afterCount = paymentRepository.count();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Payment reseeding completed");
        response.put("orders", orders.size());
        response.put("paymentsBefore", beforeCount);
        response.put("paymentsAfter", afterCount);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/payment-stats")
    public ResponseEntity<?> getPaymentStats() {
        List<Order> orders = orderRepository.findAll();
        List<Payment> payments = paymentRepository.findAll();
        
        int ordersWithPayments = 0;
        int ordersWithoutPayments = 0;
        
        for (Order order : orders) {
            boolean hasPayment = false;
            for (Payment payment : payments) {
                if (payment.getOrder().getOrderId().equals(order.getOrderId())) {
                    hasPayment = true;
                    break;
                }
            }
            
            if (hasPayment) {
                ordersWithPayments++;
            } else {
                ordersWithoutPayments++;
            }
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", orders.size());
        stats.put("totalPayments", payments.size());
        stats.put("ordersWithPayments", ordersWithPayments);
        stats.put("ordersWithoutPayments", ordersWithoutPayments);
        
        return ResponseEntity.ok(stats);
    }
}
