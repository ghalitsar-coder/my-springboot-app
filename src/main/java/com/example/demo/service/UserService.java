package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
      public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        return userRepository.save(user);
    }    public User updateUser(String id, User userDetails) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setFullName(userDetails.getFullName());
        user.setEmail(userDetails.getEmail());
        user.setPhoneNumber(userDetails.getPhoneNumber());
        user.setAddress(userDetails.getAddress());
        
        // Update role if provided
        if (userDetails.getRole() != null) {
            user.setRole(userDetails.getRole());
        }
        
        return userRepository.save(user);
    }    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
    
    // Role-based methods
    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }
      public boolean isAdmin(String userId) {
        return getUserById(userId)
            .map(user -> user.getRole() == UserRole.ADMIN)
            .orElse(false);
    }
      public boolean isCashier(String userId) {
        return getUserById(userId)
            .map(user -> user.getRole() == UserRole.CASHIER)
            .orElse(false);
    }
      public boolean hasAdminOrCashierRole(String userId) {
        return getUserById(userId)
            .map(user -> user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.CASHIER)
            .orElse(false);
    }
}
