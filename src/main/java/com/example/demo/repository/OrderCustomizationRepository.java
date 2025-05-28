package com.example.demo.repository;

import com.example.demo.entity.OrderCustomization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderCustomizationRepository extends JpaRepository<OrderCustomization, Long> {
    // Find customizations by order detail ID
    List<OrderCustomization> findByOrderDetailDetailId(Long detailId);
}
