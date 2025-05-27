-- Sample data untuk testing
INSERT INTO categories (name, description) VALUES
('Food', 'Various food items'),
('Beverages', 'Drinks and beverages'),
('Desserts', 'Sweet treats and desserts');

INSERT INTO users (username, password, email, full_name, phone_number, address) VALUES
('john_doe', 'password123', 'john@example.com', 'John Doe', '081234567890', 'Jl. Sudirman No. 1'),
('jane_smith', 'password456', 'jane@example.com', 'Jane Smith', '081234567891', 'Jl. Thamrin No. 2'),
('admin', 'admin123', 'admin@example.com', 'Administrator', '081234567892', 'Jl. Admin No. 3');

INSERT INTO products (category_id, name, description, price, stock, is_available) VALUES
(1, 'Nasi Goreng', 'Delicious fried rice with vegetables', 25000.00, 50, true),
(1, 'Mie Ayam', 'Chicken noodle soup', 20000.00, 30, true),
(1, 'Gado-gado', 'Indonesian vegetable salad', 18000.00, 25, true),
(2, 'Es Teh', 'Iced tea', 5000.00, 100, true),
(2, 'Kopi', 'Indonesian coffee', 8000.00, 80, true),
(2, 'Jus Jeruk', 'Fresh orange juice', 12000.00, 40, true),
(3, 'Es Krim', 'Vanilla ice cream', 15000.00, 20, true),
(3, 'Puding', 'Chocolate pudding', 10000.00, 35, true);

INSERT INTO customizations (name, price_adjustment, description) VALUES
('Extra Spicy', 0.00, 'Make it extra spicy'),
('Less Salt', 0.00, 'Reduce salt content'),
('Extra Portion', 5000.00, 'Increase portion size'),
('No MSG', 0.00, 'No monosodium glutamate'),
('Extra Sauce', 2000.00, 'Additional sauce');

INSERT INTO promotions (name, description, discount_value, start_date, end_date, is_active) VALUES
('Weekend Special', '20% off on weekends', 20.00, '2025-05-24', '2025-05-31', true),
('New Customer', '15% off for first order', 15.00, '2025-05-01', '2025-12-31', true),
('Bulk Order', '10% off for orders above 100k', 10.00, '2025-05-01', '2025-12-31', true);
