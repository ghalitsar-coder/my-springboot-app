package com.example.demo.controller;

import com.example.demo.dto.ErrorResponseDTO;
import com.example.demo.entity.Category;
import com.example.demo.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;    // ✅ SOLUSI 1: Menggunakan Map untuk POST requests (ignore Jackson relations)
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Map<String, Object> categoryData) {
        Category category = new Category();
        category.setName((String) categoryData.get("name"));
        category.setDescription((String) categoryData.get("description"));
        
        Category savedCategory = categoryRepository.save(category);
        return ResponseEntity.ok(savedCategory);
    }

    // ✅ GET - Tidak ada masalah, Jackson annotations bekerja normal untuk serialization
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        return category.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }    // ✅ SOLUSI 1: Menggunakan Map untuk PUT requests (ignore Jackson relations)  
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, 
                                                  @RequestBody Map<String, Object> categoryData) {
        return categoryRepository.findById(id)
                .map(category -> {
                    if (categoryData.containsKey("name")) {
                        category.setName((String) categoryData.get("name"));
                    }
                    if (categoryData.containsKey("description")) {
                        category.setDescription((String) categoryData.get("description"));
                    }
                    Category updatedCategory = categoryRepository.save(category);
                    return ResponseEntity.ok(updatedCategory);
                })
                .orElse(ResponseEntity.notFound().build());
    }    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            return categoryRepository.findById(id)
                    .map(category -> {
                        // Check if category has associated products
                        if (!category.getProducts().isEmpty()) {
                            ErrorResponseDTO error = new ErrorResponseDTO(400, "Bad Request", 
                                "Cannot delete category because it has " + category.getProducts().size() + " associated products. Please reassign or delete the products first.", 
                                "/api/categories/" + id);
                            return ResponseEntity.badRequest().body(error);
                        }
                        
                        categoryRepository.delete(category);
                        return ResponseEntity.ok().build();
                    })
                    .orElse(ResponseEntity.notFound().build());
                    
        } catch (Exception e) {
            ErrorResponseDTO error = new ErrorResponseDTO(500, "Internal Server Error",
                "An error occurred while deleting the category: " + e.getMessage(), 
                "/api/categories/" + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
