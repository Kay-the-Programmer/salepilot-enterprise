-- V6__create_customer_sales_tables.sql
-- Create customers, sales, sale_items, payments, returns, and return_items tables

CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    store_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(50),
    address JSONB,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    store_credit DECIMAL(10,2) NOT NULL DEFAULT 0,
    account_balance DECIMAL(10,2) NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_customers_store_id ON customers(store_id);
CREATE INDEX idx_customers_store_id_created_at ON customers(store_id, created_at);

CREATE TABLE sales (
    id BIGSERIAL PRIMARY KEY,
    store_id VARCHAR(255) NOT NULL,
    transaction_id VARCHAR(100) NOT NULL UNIQUE,
    timestamp TIMESTAMP NOT NULL,
    customer_id BIGINT,
    total DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    tax DECIMAL(10,2) NOT NULL,
    discount DECIMAL(10,2) NOT NULL DEFAULT 0,
    store_credit_used DECIMAL(10,2),
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PAID',
    fulfillment_status VARCHAR(20) NOT NULL DEFAULT 'FULFILLED',
    channel VARCHAR(20) NOT NULL DEFAULT 'POS',
    customer_details JSONB,
    amount_paid DECIMAL(10,2) NOT NULL,
    due_date DATE,
    refund_status VARCHAR(30) NOT NULL DEFAULT 'NONE',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0,
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE INDEX idx_sales_store_id ON sales(store_id);
CREATE INDEX idx_sales_store_id_timestamp ON sales(store_id, timestamp);
CREATE INDEX idx_sales_fulfillment_status ON sales(fulfillment_status);
CREATE INDEX idx_sales_customer_id ON sales(customer_id);

CREATE TABLE sale_items (
    id BIGSERIAL PRIMARY KEY,
    store_id VARCHAR(255) NOT NULL,
    sale_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity DECIMAL(10,3) NOT NULL,
    price_at_sale DECIMAL(10,2) NOT NULL,
    cost_at_sale DECIMAL(10,2),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0,
    FOREIGN KEY (sale_id) REFERENCES sales(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE INDEX idx_sale_items_store_id ON sale_items(store_id);
CREATE INDEX idx_sale_items_store_id_sale_id ON sale_items(store_id, sale_id);
CREATE INDEX idx_sale_items_product_id ON sale_items(product_id);

CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    store_id VARCHAR(255) NOT NULL,
    payment_id VARCHAR(100) NOT NULL UNIQUE,
    sale_id BIGINT NOT NULL,
    date TIMESTAMP NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    method VARCHAR(100) NOT NULL,
    reference VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0,
    FOREIGN KEY (sale_id) REFERENCES sales(id)
);

CREATE INDEX idx_payments_store_id ON payments(store_id);
CREATE INDEX idx_payments_store_id_date ON payments(store_id, date);
CREATE INDEX idx_payments_sale_id ON payments(sale_id);

CREATE TABLE returns (
    id BIGSERIAL PRIMARY KEY,
    store_id VARCHAR(255) NOT NULL,
    return_id VARCHAR(100) NOT NULL UNIQUE,
    original_sale_id BIGINT NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    refund_amount DECIMAL(10,2) NOT NULL,
    refund_method VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0,
    FOREIGN KEY (original_sale_id) REFERENCES sales(id)
);

CREATE INDEX idx_returns_store_id ON returns(store_id);
CREATE INDEX idx_returns_original_sale_id ON returns(original_sale_id);

CREATE TABLE return_items (
    id BIGSERIAL PRIMARY KEY,
    store_id VARCHAR(255) NOT NULL,
    return_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity DECIMAL(10,3) NOT NULL,
    reason TEXT,
    add_to_stock BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0,
    FOREIGN KEY (return_id) REFERENCES returns(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE INDEX idx_return_items_store_id ON return_items(store_id);
CREATE INDEX idx_return_items_return_id ON return_items(return_id);
CREATE INDEX idx_return_items_product_id ON return_items(product_id);
