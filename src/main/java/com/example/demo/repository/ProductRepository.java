package com.example.demo.repository;

import com.example.demo.entity.Product;
import com.example.demo.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(Category category);
    List<Product> findByIsAvailableTrue();
    List<Product> findByCategoryAndIsAvailableTrue(Category category);
    
    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1%")
    List<Product> findByNameContaining(String name);
}
