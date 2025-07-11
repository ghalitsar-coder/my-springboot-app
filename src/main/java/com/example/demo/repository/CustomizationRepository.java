package com.example.demo.repository;

import com.example.demo.entity.Customization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomizationRepository extends JpaRepository<Customization, Long> {
    // Add any custom query methods here if needed
}
