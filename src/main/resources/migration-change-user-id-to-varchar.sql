-- Migration to change users.id from SERIAL/BIGINT to VARCHAR(255)
-- This migration should be run manually or through a migration tool

-- WARNING: This migration will require downtime and data backup
-- Make sure to backup your database before running this migration

-- Step 1: Drop foreign key constraints that reference users.id
-- We need to drop and recreate foreign keys because we're changing the data type

-- Drop foreign key from orders table
ALTER TABLE public.orders DROP CONSTRAINT IF EXISTS orders_user_id_fkey;
ALTER TABLE public.orders DROP CONSTRAINT IF EXISTS fk_orders_user_id;

-- Step 2: Drop the sequence (since we're moving away from auto-increment)
DROP SEQUENCE IF EXISTS public.users_id_seq CASCADE;
DROP SEQUENCE IF EXISTS public.users_user_id_seq CASCADE;

-- Step 3: Change the data type of id column in users table
-- First, convert existing numeric IDs to string format with prefix
UPDATE public.users SET id = CASE 
    WHEN id = 1 THEN 'ADMIN_001'
    WHEN id = 2 THEN 'CASHIER_001' 
    WHEN id = 3 THEN 'CUSTOMER_001'
    WHEN id = 4 THEN 'CUSTOMER_002'
    ELSE 'USER_' || LPAD(id::text, 3, '0')
END;

-- Update corresponding user_id in orders table to match new string format
UPDATE public.orders SET user_id = CASE 
    WHEN user_id = 1 THEN 'ADMIN_001'
    WHEN user_id = 2 THEN 'CASHIER_001'
    WHEN user_id = 3 THEN 'CUSTOMER_001' 
    WHEN user_id = 4 THEN 'CUSTOMER_002'
    ELSE 'USER_' || LPAD(user_id::text, 3, '0')
END;

-- Step 4: Change the column data type
ALTER TABLE public.users ALTER COLUMN id TYPE VARCHAR(255);
ALTER TABLE public.orders ALTER COLUMN user_id TYPE VARCHAR(255);

-- Step 5: Recreate foreign key constraints
ALTER TABLE public.orders 
ADD CONSTRAINT fk_orders_user_id 
FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;

-- Step 6: Verify the migration
-- SELECT column_name, data_type, character_maximum_length 
-- FROM information_schema.columns 
-- WHERE table_name IN ('users', 'orders') 
-- AND column_name IN ('id', 'user_id') 
-- AND table_schema = 'public';

-- Step 7: Check data integrity
-- SELECT u.id, u.username, COUNT(o.order_id) as order_count
-- FROM public.users u
-- LEFT JOIN public.orders o ON u.id = o.user_id
-- GROUP BY u.id, u.username;
