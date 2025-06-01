package com.example.demo.dto;

/**
 * DTO untuk Category - tidak ada Jackson annotations, aman untuk POST/PUT
 */
public class CategoryDTO {
    private Long categoryId;
    private String name;
    private String description;

    // Constructors
    public CategoryDTO() {}

    public CategoryDTO(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
