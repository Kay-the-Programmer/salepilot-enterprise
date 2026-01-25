-- V3__create_stores_and_settings.sql
-- Create stores and store_settings tables

CREATE TABLE stores (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    subscription_status VARCHAR(50) NOT NULL DEFAULT 'TRIAL',
    subscription_ends_at TIMESTAMP,
    is_verified BOOLEAN DEFAULT FALSE,
    verification_documents JSONB,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_stores_status ON stores(status);
CREATE INDEX idx_stores_subscription_status ON stores(subscription_status);

CREATE TABLE store_settings (
    store_id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address TEXT,
    phone VARCHAR(50),
    email VARCHAR(255),
    website VARCHAR(255),
    tax_rate DECIMAL(5,2) NOT NULL,
    currency JSONB NOT NULL,
    receipt_message TEXT,
    low_stock_threshold INTEGER NOT NULL DEFAULT 10,
    sku_prefix VARCHAR(20),
    enable_store_credit BOOLEAN NOT NULL DEFAULT TRUE,
    payment_methods JSONB,
    supplier_payment_methods JSONB,
    is_online_store_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    tax_account_id VARCHAR(255),
    revenue_account_id VARCHAR(255),
    cogs_account_id VARCHAR(255),
    inventory_account_id VARCHAR(255),
    cash_account_id VARCHAR(255),
    ar_account_id VARCHAR(255),
    ap_account_id VARCHAR(255)
);
