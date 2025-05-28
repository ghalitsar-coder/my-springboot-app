package com.example.demo.repository;

import com.example.demo.entity.OrderCustomization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderCustomizationRepository extends JpaRepository<OrderCustomization, Long> {
    // Add any custom query methods here if needed
}
