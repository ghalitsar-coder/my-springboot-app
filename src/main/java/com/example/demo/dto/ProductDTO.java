package com.example.demo.dto;

import java.math.BigDecimal;

/**
 * DTO untuk Product - tidak ada Jackson annotations, aman untuk POST/PUT
 */
public class ProductDTO {
    private Long productId;
    private Long categoryId; // Hanya ID, bukan object Category
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Boolean isAvailable;

    // Constructors
    public ProductDTO() {}

    public ProductDTO(String name, String description, BigDecimal price, Integer stock, Long categoryId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.categoryId = categoryId;
        this.isAvailable = true;
    }

    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }
}
