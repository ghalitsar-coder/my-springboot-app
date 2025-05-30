package com.example.demo.repository;

import com.example.demo.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Find payments by order ID
    List<Payment> findByOrderOrderId(Long orderId);
    
    // Count payments by order ID
    long countByOrderOrderId(Long orderId);
    
    // Find orders without payments
    @Query("SELECT o.orderId FROM Order o WHERE NOT EXISTS (SELECT p FROM Payment p WHERE p.order.orderId = o.orderId)")
    List<Long> findOrderIdsWithoutPayments();
}
