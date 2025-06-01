package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if we already have users
        if (userRepository.count() == 0) {
            seedUsers();
        }
    }    private void seedUsers() {
        System.out.println("Seeding users data...");
        
        try {            // Check if admin user exists
            if (!userRepository.existsByUsername("admin")) {
                // Create admin user
                User admin = new User();
                admin.setId("ADMIN_001"); // Set explicit String ID
                admin.setUsername("admin");
                admin.setPassword("admin123"); // Plain text for now
                admin.setEmail("admin@coffeeshop.com");
                admin.setFullName("System Administrator");
                admin.setPhoneNumber("+6281234567890");
                admin.setAddress("Jakarta, Indonesia");
                admin.setRole(UserRole.ADMIN);
                userRepository.save(admin);
                System.out.println("Admin user created with ID: ADMIN_001");
            }
              // Check if cashier user exists
            if (!userRepository.existsByUsername("cashier")) {
                // Create cashier user
                User cashier = new User();
                cashier.setId("CASHIER_001"); // Set explicit String ID
                cashier.setUsername("cashier");
                cashier.setPassword("cashier123"); // Plain text for now
                cashier.setEmail("cashier@coffeeshop.com");
                cashier.setFullName("Coffee Cashier");
                cashier.setPhoneNumber("+6281234567891");
                cashier.setAddress("Jakarta, Indonesia");
                cashier.setRole(UserRole.CASHIER);
                userRepository.save(cashier);
                System.out.println("Cashier user created with ID: CASHIER_001");
            }
              // Check if first customer user exists
            if (!userRepository.existsByUsername("john")) {
                // Create customer user
                User customer1 = new User();
                customer1.setId("CUSTOMER_001"); // Set explicit String ID
                customer1.setUsername("john");
                customer1.setPassword("john123"); // Plain text for now
                customer1.setEmail("john@example.com");
                customer1.setFullName("John Doe");
                customer1.setPhoneNumber("+6281234567892");
                customer1.setAddress("Bandung, Indonesia");
                customer1.setRole(UserRole.CUSTOMER);
                userRepository.save(customer1);
                System.out.println("Customer 1 (John) created with ID: CUSTOMER_001");
            }
              // Check if second customer user exists
            if (!userRepository.existsByUsername("jane")) {
                // Create customer user
                User customer2 = new User();
                customer2.setId("CUSTOMER_002"); // Set explicit String ID
                customer2.setUsername("jane");
                customer2.setPassword("jane123"); // Plain text for now
                customer2.setEmail("jane@example.com");
                customer2.setFullName("Jane Smith");
                customer2.setPhoneNumber("+6281234567893");
                customer2.setAddress("Surabaya, Indonesia");
                customer2.setRole(UserRole.CUSTOMER);
                userRepository.save(customer2);
                System.out.println("Customer 2 (Jane) created with ID: CUSTOMER_002");
            }
        } catch (Exception e) {
            System.err.println("Error seeding users: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("Users data seeded successfully!");
    }
}
