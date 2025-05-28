package com.example.demo.repository;

import com.example.demo.entity.OrderPromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderPromotionRepository extends JpaRepository<OrderPromotion, Long> {
    // Add any custom query methods here if needed
}
