package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
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
    
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.ok(createdUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
      @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
      @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }@GetMapping("/test")
    public String testEndpoint() {
        return "Hello from Spring Boot! UserController test endpoint is working! 🚀";
    }
    
    // Role-based endpoints
    @GetMapping("/role/{role}")
    public List<User> getUsersByRole(@PathVariable String role) {
        try {
            UserRole userRole = UserRole.fromString(role);
            return userService.getUsersByRole(userRole);
        } catch (IllegalArgumentException e) {
            return List.of(); // Return empty list for invalid role
        }
    }
      @GetMapping("/{id}/check-role")
    public ResponseEntity<Map<String, Boolean>> checkUserRole(@PathVariable String id) {
        return userService.getUserById(id).map(user -> {
            Map<String, Boolean> roleStatus = Map.of(
                "isAdmin", user.getRole() == UserRole.ADMIN,
                "isCashier", user.getRole() == UserRole.CASHIER,
                "isCustomer", user.getRole() == UserRole.CUSTOMER
            );
            return ResponseEntity.ok(roleStatus);
        }).orElse(ResponseEntity.notFound().build());
    }
      @PutMapping("/{id}/role")
    public ResponseEntity<User> updateUserRole(@PathVariable String id, @RequestBody Map<String, String> roleRequest) {
        try {
            String roleStr = roleRequest.get("role");
            if (roleStr == null) {
                return ResponseEntity.badRequest().build();
            }
            
            UserRole role = UserRole.fromString(roleStr);
            return userService.getUserById(id).map(user -> {
                user.setRole(role);
                User updatedUser = userService.updateUser(id, user);
                return ResponseEntity.ok(updatedUser);
            }).orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
