-- Migration to remove role column from Users table
-- This migration removes the role functionality completely

-- First, drop the index on role column
DROP INDEX IF EXISTS idx_users_role;

-- Drop the role column from Users table
ALTER TABLE Users DROP COLUMN IF EXISTS role;

-- Drop the user_role enum type (only if no other tables are using it)
-- Note: This will fail if any other tables still reference this type
DROP TYPE IF EXISTS user_role;
