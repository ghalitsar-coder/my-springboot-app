-- Migration script untuk menambahkan role ke tabel Users
-- Jalankan script ini setelah schema.sql

-- Tambahkan tipe ENUM untuk role (jika belum ada)
DO $$ BEGIN
    CREATE TYPE user_role AS ENUM ('customer', 'admin', 'cashier');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- Modifikasi tabel Users (jika belum ada kolom role)
ALTER TABLE Users
ADD COLUMN IF NOT EXISTS role user_role NOT NULL DEFAULT 'customer';

-- Perbarui indeks jika diperlukan
CREATE INDEX IF NOT EXISTS idx_users_role ON Users(role);

-- Insert data contoh dengan role
INSERT INTO Users (username, password, email, full_name, phone_number, address, role) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IVooxVfN6/S8LZKPHGrLUCxO6gAOae', 'admin@lavazza.com', 'System Administrator', '+62812345678', 'Jakarta, Indonesia', 'admin'),
('cashier1', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IVooxVfN6/S8LZKPHGrLUCxO6gAOae', 'cashier1@lavazza.com', 'Maria Kasir', '+62823456789', 'Jakarta, Indonesia', 'cashier'),
('customer1', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IVooxVfN6/S8LZKPHGrLUCxO6gAOae', 'customer1@gmail.com', 'John Customer', '+62834567890', 'Bandung, Indonesia', 'customer')
ON CONFLICT (username) DO NOTHING;

-- Update existing users tanpa role menjadi customer (jika ada)
UPDATE Users SET role = 'customer' WHERE role IS NULL;
