package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/hello")
    public String getHello() {
        return "Hello from Spring Boot! API connection is working! 🚀";
    }
    
    @GetMapping("/status")
    public String getStatus() {
        return "Spring Boot server is running successfully!";
    }
}