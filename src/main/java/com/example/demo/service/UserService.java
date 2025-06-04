package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.exception.InvalidUserDataException;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
      public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }
      public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }    public User createUser(User user) {
        // Validate required fields
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new InvalidUserDataException("Username is required");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new InvalidUserDataException("Email is required");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new InvalidUserDataException("Password is required");
        }
        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            throw new InvalidUserDataException("Full name is required");
        }
        
        // Check for existing username
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException("Username '" + user.getUsername() + "' already exists");
        }
        
        // Check for existing email
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("Email '" + user.getEmail() + "' already exists");
        }
        
        // Encrypt password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        return userRepository.save(user);
    }public User updateUser(String id, User userDetails) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        
        // Validate data if being updated
        if (userDetails.getEmail() != null && userDetails.getEmail().trim().isEmpty()) {
            throw new InvalidUserDataException("Email cannot be empty");
        }
        if (userDetails.getFullName() != null && userDetails.getFullName().trim().isEmpty()) {
            throw new InvalidUserDataException("Full name cannot be empty");
        }        user.setFullName(userDetails.getFullName());
        user.setEmail(userDetails.getEmail());
        user.setPhoneNumber(userDetails.getPhoneNumber());
        user.setAddress(userDetails.getAddress());
        user.setRole(userDetails.getRole());
        
        return userRepository.save(user);
    }    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
}
