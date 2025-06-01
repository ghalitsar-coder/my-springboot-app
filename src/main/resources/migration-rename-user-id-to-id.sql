-- Migration to rename user_id column to id in users table
-- This migration should be run manually or through a migration tool

-- Step 1: Rename the primary key column in users table
ALTER TABLE public.users RENAME COLUMN user_id TO id;

-- Step 2: Update the sequence name if it exists (PostgreSQL)
-- Note: This may not be necessary depending on your PostgreSQL version
-- ALTER SEQUENCE public.users_user_id_seq RENAME TO users_id_seq;

-- Step 3: Update any indexes that reference the old column name
-- PostgreSQL automatically handles index updates when renaming columns

-- Step 4: Verify the foreign key relationships are still intact
-- The foreign key constraints should automatically point to the new column name

-- Verification queries (run these to check the migration worked):
-- SELECT column_name, data_type FROM information_schema.columns WHERE table_name = 'users' AND table_schema = 'public';
-- SELECT conname, conrelid::regclass, confrelid::regclass FROM pg_constraint WHERE confrelid = 'public.users'::regclass;
