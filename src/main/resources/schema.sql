-- Membuat tipe ENUM untuk status dan metode pembayaran (dengan handling jika sudah ada)
DO $$
BEGIN
    CREATE TYPE order_status AS ENUM ('pending', 'prepared', 'delivered', 'cancelled');
EXCEPTION
    WHEN duplicate_object THEN null;
END
$$;

DO $$
BEGIN
    CREATE TYPE payment_type AS ENUM ('cash', 'card', 'digital');
EXCEPTION
    WHEN duplicate_object THEN null;
END
$$;

DO $$
BEGIN
    CREATE TYPE payment_status AS ENUM ('pending', 'completed', 'failed');
EXCEPTION
    WHEN duplicate_object THEN null;
END
$$;

-- Tambahkan tipe ENUM untuk role
DO $$
BEGIN
    CREATE TYPE user_role AS ENUM ('customer', 'admin', 'cashier');
EXCEPTION
    WHEN duplicate_object THEN null;
END
$$;

-- Tabel Users
CREATE TABLE Users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    address TEXT,
    role user_role NOT NULL DEFAULT 'customer'
);

-- Tabel Categories
CREATE TABLE Categories (
    category_id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT
);

-- Tabel Products
CREATE TABLE Products (
    product_id SERIAL PRIMARY KEY,
    category_id INTEGER NOT NULL REFERENCES Categories(category_id),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    is_available BOOLEAN NOT NULL DEFAULT TRUE
);

-- Tabel Orders
CREATE TABLE Orders (
    order_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES Users(user_id),
    order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status order_status NOT NULL
);

-- Tabel OrderDetails
CREATE TABLE OrderDetails (
    detail_id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL REFERENCES Orders(order_id),
    product_id INTEGER NOT NULL REFERENCES Products(product_id),
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10, 2) NOT NULL,
    discount DECIMAL(10, 2) NOT NULL DEFAULT 0
);

-- Tabel Payments
CREATE TABLE Payments (
    payment_id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL REFERENCES Orders(order_id),
    type payment_type NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    status payment_status NOT NULL,
    payment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    transaction_id VARCHAR(100)
);

-- Tabel Customizations
CREATE TABLE Customizations (
    customization_id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    price_adjustment DECIMAL(10, 2) NOT NULL DEFAULT 0,
    description TEXT
);

-- Tabel OrderCustomizations
CREATE TABLE OrderCustomizations (
    order_customization_id SERIAL PRIMARY KEY,
    detail_id INTEGER NOT NULL REFERENCES OrderDetails(detail_id),
    customization_id INTEGER NOT NULL REFERENCES Customizations(customization_id)
);

-- Tabel Promotions
CREATE TABLE Promotions (
    promotion_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    discount_value DECIMAL(10, 2) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Tabel OrderPromotions
CREATE TABLE OrderPromotions (
    order_promotion_id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL REFERENCES Orders(order_id),
    promotion_id INTEGER NOT NULL REFERENCES Promotions(promotion_id)
);

-- Tabel Reviews
CREATE TABLE Reviews (
    review_id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL REFERENCES Orders(order_id),
    rating INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT
);

-- Indeks untuk performa
CREATE INDEX idx_order_user_id ON Orders(user_id);
CREATE INDEX idx_orderdetails_order_id ON OrderDetails(order_id);
CREATE INDEX idx_orderdetails_product_id ON OrderDetails(product_id);
CREATE INDEX idx_payments_order_id ON Payments(order_id);
CREATE INDEX idx_ordercustomizations_detail_id ON OrderCustomizations(detail_id);
CREATE INDEX idx_orderpromotions_order_id ON OrderPromotions(order_id);
CREATE INDEX idx_users_role ON Users(role);
