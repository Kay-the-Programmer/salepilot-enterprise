-- Seed initial data for roles and permissions
-- Version: V2__Seed_Initial_Data.sql

-- Insert default roles
INSERT INTO roles (name, description, created_by) VALUES
    ('ROLE_USER', 'Standard user role with basic permissions', 'system'),
    ('ROLE_MANAGER', 'Manager role with elevated permissions', 'system'),
    ('ROLE_ADMIN', 'Administrator role with full permissions', 'system');

-- Insert default permissions
INSERT INTO permissions (name, description, resource, action, created_by) VALUES
    -- User permissions
    ('READ_USER', 'Read user information', 'user', 'read', 'system'),
    ('CREATE_USER', 'Create new users', 'user', 'create', 'system'),
    ('UPDATE_USER', 'Update user information', 'user', 'update', 'system'),
    ('DELETE_USER', 'Delete users', 'user', 'delete', 'system'),
    
    -- Product permissions (placeholder for future)
    ('READ_PRODUCT', 'Read product information', 'product', 'read', 'system'),
    ('CREATE_PRODUCT', 'Create new products', 'product', 'create', 'system'),
    ('UPDATE_PRODUCT', 'Update product information', 'product', 'update', 'system'),
    ('DELETE_PRODUCT', 'Delete products', 'product', 'delete', 'system'),
    
    -- Order permissions (placeholder for future)
    ('READ_ORDER', 'Read order information', 'order', 'read', 'system'),
    ('CREATE_ORDER', 'Create new orders', 'order', 'create', 'system'),
    ('UPDATE_ORDER', 'Update order information', 'order', 'update', 'system'),
    ('DELETE_ORDER', 'Delete orders', 'order', 'delete', 'system');

-- Assign permissions to roles
-- ROLE_USER permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ROLE_USER' AND p.name IN (
    'READ_USER', 'READ_PRODUCT', 'READ_ORDER'
);

-- ROLE_MANAGER permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ROLE_MANAGER' AND p.name IN (
    'READ_USER', 'UPDATE_USER',
    'READ_PRODUCT', 'CREATE_PRODUCT', 'UPDATE_PRODUCT',
    'READ_ORDER', 'CREATE_ORDER', 'UPDATE_ORDER'
);

-- ROLE_ADMIN permissions (all permissions)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ROLE_ADMIN';

-- Create default admin user (password: Admin@123)
-- Note: Password is BCrypt hashed with strength 12
INSERT INTO users (username, email, password, first_name, last_name, enabled, created_by)
VALUES (
    'admin',
    'admin@salepilot.com',
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5rdHnO0YRsQMG',
    'Admin',
    'User',
    TRUE,
    'system'
);

-- Assign admin role to admin user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN';
