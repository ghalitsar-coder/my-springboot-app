package com.example.demo.controller;

import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schema")
public class SchemaTestController {
    
    @Autowired
    private ProductRepository productRepository;
    
    @GetMapping("/info")
    public Map<String, Object> getSchemaInfo() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Object[]> schemaInfo = productRepository.getSchemaInfo();
            response.put("success", true);
            response.put("tablesInPublicSchema", schemaInfo.size());
            response.put("tables", schemaInfo);
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return response;
        }
    }
    
    @GetMapping("/test")
    public Map<String, String> testSchemaConnection() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Schema configuration test endpoint");
        response.put("status", "active");
        response.put("schema", "public");
        return response;
    }
}
