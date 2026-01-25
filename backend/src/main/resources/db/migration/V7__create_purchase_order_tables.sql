-- V7__create_purchase_order_tables.sql
-- Create purchase orders, PO items, PO receptions, and stock take tables

CREATE TABLE purchase_orders (
    id BIGSERIAL PRIMARY KEY,
    store_id VARCHAR(255) NOT NULL,
    po_number VARCHAR(100) NOT NULL UNIQUE,
    supplier_id BIGINT NOT NULL,
    supplier_name VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    ordered_at TIMESTAMP,
    expected_at TIMESTAMP,
    received_at TIMESTAMP,
    notes TEXT,
    subtotal DECIMAL(10,2) NOT NULL,
    shipping_cost DECIMAL(10,2) NOT NULL DEFAULT 0,
    tax DECIMAL(10,2) NOT NULL DEFAULT 0,
    total DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
);

CREATE INDEX idx_purchase_orders_store_id ON purchase_orders(store_id);
CREATE INDEX idx_purchase_orders_store_id_created_at ON purchase_orders(store_id, created_at);
CREATE INDEX idx_purchase_orders_po_number ON purchase_orders(po_number);
CREATE INDEX idx_purchase_orders_supplier_id ON purchase_orders(supplier_id);

CREATE TABLE purchase_order_items (
    id BIGSERIAL PRIMARY KEY,
    store_id VARCHAR(255) NOT NULL,
    po_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    sku VARCHAR(100) NOT NULL,
    quantity DECIMAL(10,3) NOT NULL,
    cost_price DECIMAL(10,2) NOT NULL,
    received_quantity DECIMAL(10,3) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0,
    FOREIGN KEY (po_id) REFERENCES purchase_orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE INDEX idx_po_items_store_id ON purchase_order_items(store_id);
CREATE INDEX idx_po_items_store_id_po_id ON purchase_order_items(store_id, po_id);
CREATE INDEX idx_po_items_product_id ON purchase_order_items(product_id);

CREATE TABLE po_receptions (
    id BIGSERIAL PRIMARY KEY,
    store_id VARCHAR(255) NOT NULL,
    po_id BIGINT NOT NULL,
    reception_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0,
    FOREIGN KEY (po_id) REFERENCES purchase_orders(id)
);

CREATE INDEX idx_po_receptions_store_id ON po_receptions(store_id);
CREATE INDEX idx_po_receptions_store_id_po_id ON po_receptions(store_id, po_id);
CREATE INDEX idx_po_receptions_reception_date ON po_receptions(reception_date);

CREATE TABLE po_reception_items (
    id BIGSERIAL PRIMARY KEY,
    store_id VARCHAR(255) NOT NULL,
    reception_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity_received DECIMAL(10,3) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0,
    FOREIGN KEY (reception_id) REFERENCES po_receptions(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE INDEX idx_po_reception_items_store_id ON po_reception_items(store_id);
CREATE INDEX idx_po_reception_items_reception_id ON po_reception_items(reception_id);
CREATE INDEX idx_po_reception_items_product_id ON po_reception_items(product_id);

CREATE TABLE stock_takes (
    id BIGSERIAL PRIMARY KEY,
    store_id VARCHAR(255) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_stock_takes_store_id ON stock_takes(store_id);
CREATE INDEX idx_stock_takes_start_time ON stock_takes(start_time);

CREATE TABLE stock_take_items (
    id BIGSERIAL PRIMARY KEY,
    store_id VARCHAR(255) NOT NULL,
    stock_take_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    sku VARCHAR(100) NOT NULL,
    expected DECIMAL(10,3) NOT NULL,
    counted DECIMAL(10,3),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0,
    FOREIGN KEY (stock_take_id) REFERENCES stock_takes(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE INDEX idx_stock_take_items_store_id ON stock_take_items(store_id);
CREATE INDEX idx_stock_take_items_stock_take_id ON stock_take_items(stock_take_id);
CREATE INDEX idx_stock_take_items_product_id ON stock_take_items(product_id);
