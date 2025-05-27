package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataSeederConfig {
    
    @Autowired
    private UserRepository userRepository;
    
    @Bean
    @Profile("!prod") // Don't run this in production
    public CommandLineRunner dataSeeder() {
        return args -> {
            // Only seed if the database is empty
            if (userRepository.count() == 0) {
                System.out.println("Seeding users data...");
                
                // Create a password encoder
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                String password = encoder.encode("password123");
                
                // Create an admin user
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(password);
                admin.setEmail("admin@coffeeshop.com");
                admin.setFullName("System Administrator");
                admin.setPhoneNumber("+6281234567890");
                admin.setAddress("Jakarta, Indonesia");
                admin.setRole(UserRole.ADMIN);
                userRepository.save(admin);
                
                // Create a cashier user
                User cashier = new User();
                cashier.setUsername("cashier");
                cashier.setPassword(password);
                cashier.setEmail("cashier@coffeeshop.com");
                cashier.setFullName("Maria Cashier");
                cashier.setPhoneNumber("+6282345678901");
                cashier.setAddress("Jakarta, Indonesia");
                cashier.setRole(UserRole.CASHIER);
                userRepository.save(cashier);
                
                // Create a regular customer
                User customer = new User();
                customer.setUsername("customer");
                customer.setPassword(password);
                customer.setEmail("customer@gmail.com");
                customer.setFullName("John Customer");
                customer.setPhoneNumber("+6283456789012");
                customer.setAddress("Bandung, Indonesia");
                customer.setRole(UserRole.CUSTOMER);
                userRepository.save(customer);
                
                // Create additional test users
                User customer2 = new User();
                customer2.setUsername("alice");
                customer2.setPassword(password);
                customer2.setEmail("alice@gmail.com");
                customer2.setFullName("Alice Johnson");
                customer2.setPhoneNumber("+6284567890123");
                customer2.setAddress("Surabaya, Indonesia");
                customer2.setRole(UserRole.CUSTOMER);
                userRepository.save(customer2);
                
                User customer3 = new User();
                customer3.setUsername("bob");
                customer3.setPassword(password);
                customer3.setEmail("bob@gmail.com");
                customer3.setFullName("Bob Smith");
                customer3.setPhoneNumber("+6285678901234");
                customer3.setAddress("Yogyakarta, Indonesia");
                customer3.setRole(UserRole.CUSTOMER);
                userRepository.save(customer3);
                
                System.out.println("Data seeding completed successfully!");
            } else {
                System.out.println("Skipping data seeding as users already exist.");
            }
        };
    }
}
