package com.example.demo.controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users-fixed")
public class UserFixedController {
    
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
    }
      // ✅ SOLUSI: Menggunakan DTO untuk input, Entity untuk output
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserDTO userDTO) {
        // Convert DTO ke Entity
        User user = new User();
        user.setId(UUID.randomUUID().toString()); // Generate UUID untuk ID
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setEmail(userDTO.getEmail());
        user.setFullName(userDTO.getFullName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setAddress(userDTO.getAddress());
        
        User savedUser = userService.createUser(user);
        return ResponseEntity.ok(savedUser);
    }    // ✅ SOLUSI: Menggunakan DTO untuk input
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody UserDTO userDTO) {
        return userService.getUserById(id)
            .map(user -> {
                if (userDTO.getUsername() != null) user.setUsername(userDTO.getUsername());
                if (userDTO.getPassword() != null) user.setPassword(userDTO.getPassword());
                if (userDTO.getEmail() != null) user.setEmail(userDTO.getEmail());
                if (userDTO.getFullName() != null) user.setFullName(userDTO.getFullName());
                if (userDTO.getPhoneNumber() != null) user.setPhoneNumber(userDTO.getPhoneNumber());
                if (userDTO.getAddress() != null) user.setAddress(userDTO.getAddress());
                
                User updatedUser = userService.updateUser(id, user);
                return ResponseEntity.ok(updatedUser);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
