package com.example.demo.controller;

import com.example.demo.dto.ErrorResponseDTO;
import com.example.demo.dto.ProductDTO;
import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {
    @Autowired
    private ProductService productService;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @GetMapping("/hello")
    public String testConnection() {
        return "Hello from Spring Boot Products! API connection is working! 🚀";
    }
    
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
            .map(product -> ResponseEntity.ok().body(product))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductDTO productDTO) {
        try {
            // Validate required fields with proper error messages
            if (productDTO.getName() == null || productDTO.getName().trim().isEmpty()) {
                ErrorResponseDTO error = new ErrorResponseDTO(400, "Bad Request", 
                    "Product name is required", "/api/products");
                return ResponseEntity.badRequest().body(error);
            }
            
            if (productDTO.getPrice() == null || productDTO.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                ErrorResponseDTO error = new ErrorResponseDTO(400, "Bad Request", 
                    "Product price must be greater than 0", "/api/products");
                return ResponseEntity.badRequest().body(error);
            }
            
            if (productDTO.getCategoryId() == null) {
                ErrorResponseDTO error = new ErrorResponseDTO(400, "Bad Request", 
                    "Category ID is required", "/api/products");
                return ResponseEntity.badRequest().body(error);
            }
            
            // Check if category exists
            Category category = categoryRepository.findById(productDTO.getCategoryId()).orElse(null);
            if (category == null) {
                ErrorResponseDTO error = new ErrorResponseDTO(404, "Not Found", 
                    "Category not found with id: " + productDTO.getCategoryId(), "/api/products");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
              // Convert DTO to Entity
            Product product = new Product();
            product.setName(productDTO.getName());
            product.setDescription(productDTO.getDescription());
            product.setPrice(productDTO.getPrice());
            product.setStock(productDTO.getStock());
            product.setIsAvailable(productDTO.getIsAvailable() != null ? productDTO.getIsAvailable() : true);
            product.setCategory(category);
            
            // Save and return
            Product savedProduct = productService.createProduct(product);
            return ResponseEntity.ok(savedProduct);
            
        } catch (Exception e) {
            ErrorResponseDTO error = new ErrorResponseDTO(500, "Internal Server Error",
                "An error occurred while creating the product: " + e.getMessage(), "/api/products");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        try {
            Optional<Product> productOpt = productService.getProductById(id);
            if (productOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(404, "Not Found", "Product not found with id: " + id, "/api/products"));
            }
            
            Product product = productOpt.get();
            // Update fields if provided
            if (productDTO.getName() != null) product.setName(productDTO.getName());
            if (productDTO.getDescription() != null) product.setDescription(productDTO.getDescription());
            if (productDTO.getPrice() != null) product.setPrice(productDTO.getPrice());
            if (productDTO.getStock() != null) product.setStock(productDTO.getStock());
            if (productDTO.getIsAvailable() != null) product.setIsAvailable(productDTO.getIsAvailable());
            
            // Update Category if provided
            if (productDTO.getCategoryId() != null) {
                Category category = categoryRepository.findById(productDTO.getCategoryId()).orElse(null);
                if (category != null) {
                    product.setCategory(category);
                }
            }
            
            Product updatedProduct = productService.updateProduct(id, product);
            return ResponseEntity.ok().body(updatedProduct);
                    
        } catch (Exception e) {
            ErrorResponseDTO error = new ErrorResponseDTO(500, "Internal Server Error",
                "An error occurred while updating the product: " + e.getMessage(), "/api/products");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }
}
