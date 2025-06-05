package com.example.demo.controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/hello")
    public String testConnection() {
        return "Hello from Fixed User Controller! Jackson annotations resolved! 🚀";
    }
    
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
      
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        return userService.getUserById(id)
            .map(user -> ResponseEntity.ok().body(user))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
            .map(user -> ResponseEntity.ok().body(user))
            .orElse(ResponseEntity.notFound().build());
    }
      @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
            .map(user -> ResponseEntity.ok().body(user))
            .orElse(ResponseEntity.notFound().build());
    }    // ✅ UPDATED: Support for BetterAuth integration - ID and password are optional
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserDTO userDTO) {
        // Convert DTO ke Entity
        User user = new User();
        
        // Use provided ID from BetterAuth, or auto-generate if not provided
        if (userDTO.getId() != null && !userDTO.getId().trim().isEmpty()) {
            user.setId(userDTO.getId()); // Use BetterAuth ID
        }
        // If no ID provided, UserService will auto-generate UUID
        
        user.setUsername(userDTO.getUsername());
        
        // Only set password if provided (for non-BetterAuth users)
        if (userDTO.getPassword() != null && !userDTO.getPassword().trim().isEmpty()) {
            user.setPassword(userDTO.getPassword());
        }
        
        user.setEmail(userDTO.getEmail());
        user.setFullName(userDTO.getFullName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setAddress(userDTO.getAddress());
        user.setRole(userDTO.getRole() != null ? userDTO.getRole() : "customer");
        
        User savedUser = userService.createUser(user);
        return ResponseEntity.ok(savedUser);
    }// ✅ SOLUSI: Menggunakan DTO untuk input
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody UserDTO userDTO) {
        return userService.getUserById(id)
            .map(user -> {
                if (userDTO.getUsername() != null) user.setUsername(userDTO.getUsername());
                if (userDTO.getPassword() != null) user.setPassword(userDTO.getPassword());
                if (userDTO.getEmail() != null) user.setEmail(userDTO.getEmail());                if (userDTO.getFullName() != null) user.setFullName(userDTO.getFullName());
                if (userDTO.getPhoneNumber() != null) user.setPhoneNumber(userDTO.getPhoneNumber());
                if (userDTO.getAddress() != null) user.setAddress(userDTO.getAddress());
                if (userDTO.getRole() != null) user.setRole(userDTO.getRole());
                
                User updatedUser = userService.updateUser(id, user);
                return ResponseEntity.ok(updatedUser);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Dedicated endpoint for BetterAuth integration
     * Expects ID from frontend, no password required
     */
    @PostMapping("/auth-sync")
    public ResponseEntity<User> createUserFromAuth(@RequestBody UserDTO userDTO) {
        try {
            // For BetterAuth integration, ID is required
            if (userDTO.getId() == null || userDTO.getId().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            // Convert DTO to Entity
            User user = new User();
            user.setId(userDTO.getId()); // Use BetterAuth provided ID
            user.setUsername(userDTO.getUsername());
            user.setEmail(userDTO.getEmail());
            user.setFullName(userDTO.getFullName());
            user.setPhoneNumber(userDTO.getPhoneNumber());
            user.setAddress(userDTO.getAddress());
            user.setRole(userDTO.getRole() != null ? userDTO.getRole() : "customer");
            // No password - handled by BetterAuth
            
            User savedUser = userService.createUser(user);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
