-- V5__create_product_tables.sql
-- Create categories, suppliers, and products tables

CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    store_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    parent_id BIGINT,
    attributes JSONB,
    revenue_account_id VARCHAR(255),
    cogs_account_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0,
    FOREIGN KEY (parent_id) REFERENCES categories(id)
);

CREATE INDEX idx_categories_store_id ON categories(store_id);
CREATE INDEX idx_categories_parent_id ON categories(parent_id);

CREATE TABLE suppliers (
    id BIGSERIAL PRIMARY KEY,
    store_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    contact_person VARCHAR(255),
    phone VARCHAR(50),
    email VARCHAR(255),
    address TEXT,
    payment_terms VARCHAR(255),
    banking_details TEXT,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_suppliers_store_id ON suppliers(store_id);

CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    store_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    sku VARCHAR(100) NOT NULL,
    barcode VARCHAR(100),
    category_id BIGINT,
    supplier_id BIGINT,
    price DECIMAL(10,2) NOT NULL,
    cost_price DECIMAL(10,2),
    stock DECIMAL(10,3) NOT NULL DEFAULT 0,
    unit_of_measure VARCHAR(20) NOT NULL DEFAULT 'UNIT',
    image_urls TEXT[],
    brand VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    reorder_point INTEGER,
    weight DECIMAL(10,3),
    dimensions VARCHAR(100),
    safety_stock INTEGER,
    variants JSONB,
    custom_attributes JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
);

CREATE INDEX idx_products_store_id ON products(store_id);
CREATE INDEX idx_products_store_id_status ON products(store_id, status);
CREATE INDEX idx_products_sku ON products(sku);
CREATE INDEX idx_products_barcode ON products(barcode);
CREATE UNIQUE INDEX uidx_products_store_sku ON products(store_id, sku);
