package com.example.demo.config;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class ComprehensiveDataSeeder {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CustomizationRepository customizationRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private PromotionRepository promotionRepository;
    
    @Autowired
    private OrderPromotionRepository orderPromotionRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private OrderCustomizationRepository orderCustomizationRepository;      @Bean
    @Profile("!prod") // Don't run this in production
    public CommandLineRunner seedData() {
        return args -> {
            System.out.println("SEEDER DEBUG: Comprehensive data seeder is running!");
            System.out.println("SEEDER DEBUG: Current user count: " + userRepository.count());
            System.out.println("SEEDER DEBUG: Current product count: " + productRepository.count());
            System.out.println("SEEDER DEBUG: Current category count: " + categoryRepository.count());
            
            // Check if we have product data, not users
            if (productRepository.count() > 0) {
                System.out.println("Database already contains products. Skipping comprehensive data seeding.");
                System.out.println("To force seeding, use -DforceSeeding=true as JVM argument or call /api/admin/seeder/force endpoint.");
                return;
            }
            
            System.out.println("Starting comprehensive data seeding...");
            
            // Seed all tables in the correct order
            List<User> users = seedUsers();
            List<Category> categories = seedCategories();
            List<Product> products = seedProducts(categories);
            List<Customization> customizations = seedCustomizations();
            List<Promotion> promotions = seedPromotions();
            List<Order> orders = seedOrders(users);
            seedOrderDetails(orders, products, customizations);
            seedPayments(orders);
            seedOrderPromotions(orders, promotions);
            seedReviews(orders);
            
            System.out.println("Comprehensive data seeding completed successfully!");
        };
    }
    
    private List<User> seedUsers() {
        System.out.println("Seeding users data...");
        List<User> users = new ArrayList<>();
        
        try {
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
            users.add(admin);
            
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
            users.add(cashier);
            
            // Create regular customers
            User customer1 = new User();
            customer1.setUsername("john");
            customer1.setPassword(password);
            customer1.setEmail("john@gmail.com");
            customer1.setFullName("John Doe");
            customer1.setPhoneNumber("+6283456789012");
            customer1.setAddress("Bandung, Indonesia");
            customer1.setRole(UserRole.CUSTOMER);
            userRepository.save(customer1);
            users.add(customer1);
            
            User customer2 = new User();
            customer2.setUsername("alice");
            customer2.setPassword(password);
            customer2.setEmail("alice@gmail.com");
            customer2.setFullName("Alice Johnson");
            customer2.setPhoneNumber("+6284567890123");
            customer2.setAddress("Surabaya, Indonesia");
            customer2.setRole(UserRole.CUSTOMER);
            userRepository.save(customer2);
            users.add(customer2);
            
            User customer3 = new User();
            customer3.setUsername("bob");
            customer3.setPassword(password);
            customer3.setEmail("bob@gmail.com");
            customer3.setFullName("Bob Smith");
            customer3.setPhoneNumber("+6285678901234");
            customer3.setAddress("Yogyakarta, Indonesia");
            customer3.setRole(UserRole.CUSTOMER);
            userRepository.save(customer3);
            users.add(customer3);
            
            System.out.println("Users data seeded successfully!");
        } catch (Exception e) {
            System.err.println("Error seeding users: " + e.getMessage());
            e.printStackTrace();
        }
        
        return users;
    }
    
    public List<Category> seedCategoriesManually() {
        return seedCategories();
    }
    
    private List<Category> seedCategories() {
        System.out.println("Seeding categories data...");
        List<Category> categories = new ArrayList<>();
        
        try {
            // Create categories
            Category coffeeCategory = new Category();
            coffeeCategory.setName("Coffee");
            coffeeCategory.setDescription("Various coffee products from around the world");
            categoryRepository.save(coffeeCategory);
            categories.add(coffeeCategory);
            
            Category teaCategory = new Category();
            teaCategory.setName("Tea");
            teaCategory.setDescription("Refreshing tea selections");
            categoryRepository.save(teaCategory);
            categories.add(teaCategory);
            
            Category pastryCategory = new Category();
            pastryCategory.setName("Pastry");
            pastryCategory.setDescription("Freshly baked pastries and desserts");
            categoryRepository.save(pastryCategory);
            categories.add(pastryCategory);
            
            Category sandwichCategory = new Category();
            sandwichCategory.setName("Sandwich");
            sandwichCategory.setDescription("Delicious sandwiches and wraps");
            categoryRepository.save(sandwichCategory);
            categories.add(sandwichCategory);
            
            Category merchandiseCategory = new Category();
            merchandiseCategory.setName("Merchandise");
            merchandiseCategory.setDescription("Coffee shop branded merchandise");
            categoryRepository.save(merchandiseCategory);
            categories.add(merchandiseCategory);
            
            System.out.println("Categories data seeded successfully!");
        } catch (Exception e) {
            System.err.println("Error seeding categories: " + e.getMessage());
            e.printStackTrace();
        }
        
        return categories;
    }
    
    public List<Product> seedProductsManually(List<Category> categories) {
        return seedProducts(categories);
    }
    
    private List<Product> seedProducts(List<Category> categories) {
        System.out.println("Seeding products data...");
        List<Product> products = new ArrayList<>();
        
        try {
            // Get categories
            Category coffeeCategory = categories.stream()
                .filter(c -> "Coffee".equals(c.getName()))
                .findFirst()
                .orElse(categories.get(0));
                
            Category teaCategory = categories.stream()
                .filter(c -> "Tea".equals(c.getName()))
                .findFirst()
                .orElse(categories.get(0));
                
            Category pastryCategory = categories.stream()
                .filter(c -> "Pastry".equals(c.getName()))
                .findFirst()
                .orElse(categories.get(0));
                
            Category sandwichCategory = categories.stream()
                .filter(c -> "Sandwich".equals(c.getName()))
                .findFirst()
                .orElse(categories.get(0));
                
            Category merchandiseCategory = categories.stream()
                .filter(c -> "Merchandise".equals(c.getName()))
                .findFirst()
                .orElse(categories.get(0));
            
            // Coffee products
            Product espresso = new Product(coffeeCategory, "Espresso", 
                "A concentrated coffee beverage brewed by forcing hot water under pressure through finely ground coffee beans.", 
                new BigDecimal("25000"), 100);
            espresso.setIsAvailable(true);
            productRepository.save(espresso);
            products.add(espresso);
            
            Product americano = new Product(coffeeCategory, "Americano", 
                "Espresso diluted with hot water, making it similar in strength to drip coffee.", 
                new BigDecimal("28000"), 100);
            americano.setIsAvailable(true);
            productRepository.save(americano);
            products.add(americano);
            
            Product latte = new Product(coffeeCategory, "Caffè Latte", 
                "Coffee drink made with espresso and steamed milk.", 
                new BigDecimal("35000"), 100);
            latte.setIsAvailable(true);
            productRepository.save(latte);
            products.add(latte);
            
            Product cappuccino = new Product(coffeeCategory, "Cappuccino", 
                "Equal parts espresso, steamed milk, and milk foam.", 
                new BigDecimal("35000"), 100);
            cappuccino.setIsAvailable(true);
            productRepository.save(cappuccino);
            products.add(cappuccino);
            
            Product mocha = new Product(coffeeCategory, "Mocha", 
                "Chocolate-flavored variant of a café latte.", 
                new BigDecimal("38000"), 100);
            mocha.setIsAvailable(true);
            productRepository.save(mocha);
            products.add(mocha);
            
            // Tea products
            Product greenTea = new Product(teaCategory, "Green Tea", 
                "A type of tea made from Camellia sinensis leaves that have not undergone the same withering and oxidation process.", 
                new BigDecimal("20000"), 100);
            greenTea.setIsAvailable(true);
            productRepository.save(greenTea);
            products.add(greenTea);
            
            Product blackTea = new Product(teaCategory, "Black Tea", 
                "A type of tea that is more oxidized than oolong, green, and white teas.", 
                new BigDecimal("20000"), 100);
            blackTea.setIsAvailable(true);
            productRepository.save(blackTea);
            products.add(blackTea);
            
            // Pastry products
            Product croissant = new Product(pastryCategory, "Croissant", 
                "A buttery, flaky pastry of Austrian origin.", 
                new BigDecimal("18000"), 50);
            croissant.setIsAvailable(true);
            productRepository.save(croissant);
            products.add(croissant);
            
            Product muffin = new Product(pastryCategory, "Chocolate Muffin", 
                "A sweet baked good with chocolate chips.", 
                new BigDecimal("22000"), 50);
            muffin.setIsAvailable(true);
            productRepository.save(muffin);
            products.add(muffin);
            
            // Sandwich products
            Product clubSandwich = new Product(sandwichCategory, "Club Sandwich", 
                "A sandwich with chicken, bacon, lettuce, tomato, and mayo.", 
                new BigDecimal("45000"), 30);
            clubSandwich.setIsAvailable(true);
            productRepository.save(clubSandwich);
            products.add(clubSandwich);
            
            // Merchandise products
            Product tumbler = new Product(merchandiseCategory, "Coffee Tumbler", 
                "A double-walled stainless steel tumbler with our logo.", 
                new BigDecimal("150000"), 20);
            tumbler.setIsAvailable(true);
            productRepository.save(tumbler);
            products.add(tumbler);
            
            System.out.println("Products data seeded successfully!");
        } catch (Exception e) {
            System.err.println("Error seeding products: " + e.getMessage());
            e.printStackTrace();
        }
        
        return products;
    }
    
    public List<Customization> seedCustomizationsManually() {
        return seedCustomizations();
    }
    
    private List<Customization> seedCustomizations() {
        System.out.println("Seeding customizations data...");
        List<Customization> customizations = new ArrayList<>();
        
        try {
            // Create coffee customizations
            Customization extraShot = new Customization("Extra Shot", new BigDecimal("8000"), 
                                               "Add an additional shot of espresso");
            customizationRepository.save(extraShot);
            customizations.add(extraShot);
            
            Customization soyMilk = new Customization("Soy Milk", new BigDecimal("5000"), 
                                            "Substitute with soy milk");
            customizationRepository.save(soyMilk);
            customizations.add(soyMilk);
            
            Customization almondMilk = new Customization("Almond Milk", new BigDecimal("7000"), 
                                                "Substitute with almond milk");
            customizationRepository.save(almondMilk);
            customizations.add(almondMilk);
            
            Customization vanillaSyrup = new Customization("Vanilla Syrup", new BigDecimal("5000"), 
                                                  "Add vanilla flavoring");
            customizationRepository.save(vanillaSyrup);
            customizations.add(vanillaSyrup);
            
            Customization caramelSyrup = new Customization("Caramel Syrup", new BigDecimal("5000"), 
                                                  "Add caramel flavoring");
            customizationRepository.save(caramelSyrup);
            customizations.add(caramelSyrup);
            
            Customization whippedCream = new Customization("Whipped Cream", new BigDecimal("3000"), 
                                                 "Add whipped cream on top");
            customizationRepository.save(whippedCream);
            customizations.add(whippedCream);
            
            Customization iceDrink = new Customization("Ice", BigDecimal.ZERO, 
                                            "Make it an iced drink");
            customizationRepository.save(iceDrink);
            customizations.add(iceDrink);
            
            System.out.println("Customizations data seeded successfully!");
        } catch (Exception e) {
            System.err.println("Error seeding customizations: " + e.getMessage());
            e.printStackTrace();
        }
        
        return customizations;
    }
    
    public List<Promotion> seedPromotionsManually() {
        return seedPromotions();
    }
    
    private List<Promotion> seedPromotions() {
        System.out.println("Seeding promotions data...");
        List<Promotion> promotions = new ArrayList<>();
        
        try {
            // Create promotions
            Promotion newCustomer = new Promotion(
                "New Customer Discount",
                "15% off for first purchase",
                new BigDecimal("0.15"),
                LocalDate.now().minusDays(30),
                LocalDate.now().plusDays(60)
            );
            newCustomer.setIsActive(true);
            promotionRepository.save(newCustomer);
            promotions.add(newCustomer);
            
            Promotion happyHour = new Promotion(
                "Happy Hour",
                "10% off all drinks between 2PM-5PM",
                new BigDecimal("0.10"),
                LocalDate.now().minusDays(15),
                LocalDate.now().plusDays(45)
            );
            happyHour.setIsActive(true);
            promotionRepository.save(happyHour);
            promotions.add(happyHour);
            
            Promotion bulkOrder = new Promotion(
                "Bulk Order",
                "20% off for orders above 200K",
                new BigDecimal("0.20"),
                LocalDate.now().minusDays(5),
                LocalDate.now().plusDays(25)
            );
            bulkOrder.setIsActive(true);
            promotionRepository.save(bulkOrder);
            promotions.add(bulkOrder);
            
            System.out.println("Promotions data seeded successfully!");
        } catch (Exception e) {
            System.err.println("Error seeding promotions: " + e.getMessage());
            e.printStackTrace();
        }
        
        return promotions;
    }
    
    public List<Order> seedOrdersManually(List<User> users) {
        return seedOrders(users);
    }
    
    private List<Order> seedOrders(List<User> users) {
        System.out.println("Seeding orders data...");
        List<Order> orders = new ArrayList<>();
        
        try {
            // Get customer users
            List<User> customers = users.stream()
                .filter(u -> UserRole.CUSTOMER.equals(u.getRole()))
                .toList();
                
            if (customers.isEmpty()) {
                throw new RuntimeException("No customers found for orders");
            }
            
            // Create completed orders
            for (int i = 0; i < 3; i++) {
                User customer = customers.get(i % customers.size());
                
                Order completedOrder = new Order(customer, OrderStatus.DELIVERED);
                completedOrder.setOrderDate(LocalDateTime.now().minusDays(i + 1));
                orderRepository.save(completedOrder);
                orders.add(completedOrder);
            }
            
            // Create pending order
            User activeCustomer = customers.get(0);
            Order pendingOrder = new Order(activeCustomer, OrderStatus.PENDING);
            pendingOrder.setOrderDate(LocalDateTime.now());
            orderRepository.save(pendingOrder);
            orders.add(pendingOrder);
            
            System.out.println("Orders data seeded successfully!");
        } catch (Exception e) {
            System.err.println("Error seeding orders: " + e.getMessage());
            e.printStackTrace();
        }
        
        return orders;
    }
    
    public void seedOrderDetailsManually(List<Order> orders, List<Product> products, List<Customization> customizations) {
        seedOrderDetails(orders, products, customizations);
    }
    
    private void seedOrderDetails(List<Order> orders, List<Product> products, List<Customization> customizations) {
        System.out.println("Seeding order details data...");
        
        try {
            int productIndex = 0;
            
            for (Order order : orders) {
                // Add 2-3 items per order
                int itemCount = 2 + (int)(Math.random() * 2);
                
                for (int i = 0; i < itemCount; i++) {
                    Product product = products.get(productIndex % products.size());
                    productIndex++;
                    
                    int quantity = 1 + (int)(Math.random() * 3);
                    
                    OrderDetail detail = new OrderDetail(order, product, quantity, product.getPrice());
                    orderDetailRepository.save(detail);
                    
                    // Add customizations to beverages (coffee/tea)
                    if ("Coffee".equals(product.getCategory().getName()) || 
                        "Tea".equals(product.getCategory().getName())) {
                        
                        // Add 0-2 customizations
                        int customizationCount = (int)(Math.random() * 3);
                          for (int j = 0; j < customizationCount && j < customizations.size(); j++) {
                            Customization customization = customizations.get(j % customizations.size());
                            OrderCustomization orderCustomization = new OrderCustomization(detail, customization);
                            orderCustomizationRepository.save(orderCustomization);
                        }
                    }
                }
            }
            
            System.out.println("Order details data seeded successfully!");
        } catch (Exception e) {
            System.err.println("Error seeding order details: " + e.getMessage());
            e.printStackTrace();
        }
    }
      public void seedPaymentsManually(List<Order> orders) {
        seedPayments(orders);
    }
    
    private void seedPayments(List<Order> orders) {
        System.out.println("Seeding payments data...");
        
        try {
            System.out.println("Found " + orders.size() + " orders to create payments for");
            
            // First, check existing payments to avoid duplicates
            long existingPaymentCount = paymentRepository.count();
            System.out.println("DEBUG: Existing payment count before seeding: " + existingPaymentCount);
            
            int paymentsCreated = 0;
            
            for (Order order : orders) {
                // Log order details
                System.out.println("Processing payment for Order ID: " + order.getOrderId() + ", Status: " + order.getStatus());
                
                // Load a fresh copy of the order with all related entities
                Order freshOrder = orderRepository.findById(order.getOrderId()).orElse(order);
                
                // Check if order already has payments
                List<Payment> existingPayments = freshOrder.getPayments();
                if (existingPayments != null && !existingPayments.isEmpty()) {
                    System.out.println("DEBUG: Order ID " + order.getOrderId() + " already has " + existingPayments.size() + " payments, skipping");
                    continue;
                }
                
                // Calculate order total with the fresh order
                BigDecimal total = calculateOrderTotal(freshOrder);
                
                if (total.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("WARNING: Calculated total for Order ID " + order.getOrderId() + " is zero or negative: " + total);
                    // Use a default amount for demo purposes
                    total = new BigDecimal("50000.00");
                    System.out.println("DEBUG: Using default amount: " + total + " for Order ID: " + order.getOrderId());
                }
                
                // Create payment
                try {
                    if (OrderStatus.DELIVERED.equals(order.getStatus())) {
                        // Completed payment for delivered orders
                        Payment payment = new Payment(freshOrder, PaymentType.DIGITAL, total, PaymentStatus.COMPLETED);
                        payment.setPaymentMethod("Credit Card");
                        payment.setTransactionId("TRX" + System.currentTimeMillis() + order.getOrderId());
                        payment.setPaymentDate(order.getOrderDate().plusMinutes(15));
                        Payment savedPayment = paymentRepository.save(payment);
                        paymentsCreated++;
                        System.out.println("Created COMPLETED payment ID: " + savedPayment.getPaymentId() + " for Order ID: " + order.getOrderId() + " with amount: " + total);
                    } else if (OrderStatus.PENDING.equals(order.getStatus())) {
                        // Pending payment
                        Payment payment = new Payment(freshOrder, PaymentType.DIGITAL, total, PaymentStatus.PENDING);
                        payment.setPaymentMethod("Virtual Account");
                        payment.setBank("BCA");
                        payment.setVaNumber("1234567890" + order.getOrderId());
                        Payment savedPayment = paymentRepository.save(payment);
                        paymentsCreated++;
                        System.out.println("Created PENDING payment ID: " + savedPayment.getPaymentId() + " for Order ID: " + order.getOrderId() + " with amount: " + total);
                    }
                } catch (Exception innerEx) {
                    System.err.println("Error creating payment for Order ID: " + order.getOrderId() + ": " + innerEx.getMessage());
                    innerEx.printStackTrace();
                }
            }
            
            // Verify payment creation
            long paymentCount = paymentRepository.count();
            System.out.println("DEBUG: Created " + paymentsCreated + " new payments");
            System.out.println("Total payments in database after seeding: " + paymentCount);
            System.out.println("Payments data seeded successfully!");
        } catch (Exception e) {
            System.err.println("Error seeding payments: " + e.getMessage());
            e.printStackTrace();
        }
    }private BigDecimal calculateOrderTotal(Order order) {
        BigDecimal total = BigDecimal.ZERO;
        
        System.out.println("DEBUG: Calculating total for Order ID: " + order.getOrderId());
        
        // Ensure OrderDetails are fetched from DB if they are null or empty in the order object
        List<OrderDetail> orderDetails = order.getOrderDetails();
        if (orderDetails == null || orderDetails.isEmpty()) {
            System.out.println("DEBUG: Order details are null or empty, fetching from repository");
            orderDetails = orderDetailRepository.findByOrderOrderId(order.getOrderId());
            System.out.println("DEBUG: Found " + orderDetails.size() + " order details from repository");
        } else {
            System.out.println("DEBUG: Found " + orderDetails.size() + " order details in order object");
        }
        
        for (OrderDetail detail : orderDetails) {
            BigDecimal itemPrice = detail.getUnitPrice();
            int quantity = detail.getQuantity();
            BigDecimal lineTotal = itemPrice.multiply(BigDecimal.valueOf(quantity));
            total = total.add(lineTotal);
            
            System.out.println("DEBUG: Detail ID: " + detail.getDetailId() + 
                              ", Product: " + (detail.getProduct() != null ? detail.getProduct().getName() : "null") + 
                              ", Quantity: " + quantity +
                              ", Unit Price: " + itemPrice +
                              ", Line Total: " + lineTotal);
            
            // Ensure OrderCustomizations are fetched from DB if they are null or empty
            List<OrderCustomization> customizations = detail.getOrderCustomizations();
            if (customizations == null || customizations.isEmpty()) {
                System.out.println("DEBUG: Customizations are null or empty for Detail ID: " + detail.getDetailId() + ", fetching from repository");
                customizations = orderCustomizationRepository.findByOrderDetailDetailId(detail.getDetailId());
                System.out.println("DEBUG: Found " + customizations.size() + " customizations from repository");
            } else {
                System.out.println("DEBUG: Found " + customizations.size() + " customizations in detail object");
            }
            
            // Add customization costs
            for (OrderCustomization customization : customizations) {
                if (customization.getCustomization() != null) {
                    BigDecimal adjustment = customization.getCustomization().getPriceAdjustment();
                    total = total.add(adjustment);
                    System.out.println("DEBUG: Added customization: " + customization.getCustomization().getName() + 
                                      ", Price Adjustment: " + adjustment);
                }
            }
        }
        
        System.out.println("Calculated total for Order ID " + order.getOrderId() + ": " + total);
        return total;
    }
    
    public void seedOrderPromotionsManually(List<Order> orders, List<Promotion> promotions) {
        seedOrderPromotions(orders, promotions);
    }
    
    private void seedOrderPromotions(List<Order> orders, List<Promotion> promotions) {
        System.out.println("Seeding order promotions data...");
        
        try {
            // Apply promotions to half of the completed orders
            for (int i = 0; i < orders.size(); i++) {
                if (i % 2 == 0 && OrderStatus.DELIVERED.equals(orders.get(i).getStatus())) {
                    Promotion promotion = promotions.get(i % promotions.size());
                    OrderPromotion orderPromotion = new OrderPromotion(orders.get(i), promotion);
                    orderPromotionRepository.save(orderPromotion);
                }
            }
            
            System.out.println("Order promotions data seeded successfully!");
        } catch (Exception e) {
            System.err.println("Error seeding order promotions: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void seedReviewsManually(List<Order> orders) {
        seedReviews(orders);
    }
    
    private void seedReviews(List<Order> orders) {
        System.out.println("Seeding reviews data...");
        
        try {
            // Add reviews to completed orders
            for (Order order : orders) {
                if (OrderStatus.DELIVERED.equals(order.getStatus())) {
                    int rating = 3 + (int)(Math.random() * 3); // Ratings 3-5
                    
                    Review review = new Review(order, rating, 
                        "Great service and delicious products! Would order again.");
                    reviewRepository.save(review);
                }
            }
            
            System.out.println("Reviews data seeded successfully!");
        } catch (Exception e) {
            System.err.println("Error seeding reviews: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
