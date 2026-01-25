-- V4__update_users_table.sql
-- Add multi-tenant and verification fields to users table

ALTER TABLE users ADD COLUMN current_store_id VARCHAR(255);
ALTER TABLE users ADD COLUMN is_verified BOOLEAN DEFAULT FALSE;
ALTER TABLE users ADD COLUMN verification_token VARCHAR(255);
ALTER TABLE users ADD COLUMN reset_password_token VARCHAR(255);
ALTER TABLE users ADD COLUMN reset_password_expires TIMESTAMP;

CREATE INDEX idx_users_current_store_id ON users(current_store_id);
CREATE INDEX idx_users_verification_token ON users(verification_token);
CREATE INDEX idx_users_reset_password_token ON users(reset_password_token);
