package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/reset-database")
    public ResponseEntity<String> resetDatabase() {
        try {
            // Disable foreign key checks to allow truncating tables with relationships
            jdbcTemplate.execute("SET session_replication_role = 'replica';");
            
            // Truncate all tables in reverse order of dependencies
            jdbcTemplate.execute("TRUNCATE TABLE reviews CASCADE;");
            jdbcTemplate.execute("TRUNCATE TABLE orderpromotions CASCADE;");
            jdbcTemplate.execute("TRUNCATE TABLE ordercustomizations CASCADE;");
            jdbcTemplate.execute("TRUNCATE TABLE payments CASCADE;");
            jdbcTemplate.execute("TRUNCATE TABLE orderdetails CASCADE;");
            jdbcTemplate.execute("TRUNCATE TABLE orders CASCADE;");
            jdbcTemplate.execute("TRUNCATE TABLE products CASCADE;");
            jdbcTemplate.execute("TRUNCATE TABLE categories CASCADE;");
            jdbcTemplate.execute("TRUNCATE TABLE promotions CASCADE;");
            jdbcTemplate.execute("TRUNCATE TABLE customizations CASCADE;");
            jdbcTemplate.execute("TRUNCATE TABLE users CASCADE;");
            
            // Re-enable foreign key checks
            jdbcTemplate.execute("SET session_replication_role = 'origin';");
            
            return ResponseEntity.ok("Database has been reset. Restart the application to reseed data.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error resetting database: " + e.getMessage());
        }
    }
}
