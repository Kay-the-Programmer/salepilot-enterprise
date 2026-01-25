-- V9__create_system_tables.sql
-- Create audit logs, offers, notifications, and subscription payment tables

CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    store_id VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    action VARCHAR(100) NOT NULL,
    details TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_audit_logs_store_id_timestamp ON audit_logs(store_id, timestamp);
CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_action ON audit_logs(action);

CREATE TABLE offers (
    id BIGSERIAL PRIMARY KEY,
    store_id VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    latitude DECIMAL(10,8) NOT NULL,
    longitude DECIMAL(11,8) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    accepted_by BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (accepted_by) REFERENCES users(id)
);

CREATE INDEX idx_offers_user_id ON offers(user_id);
CREATE INDEX idx_offers_status ON offers(status);
CREATE INDEX idx_offers_accepted_by ON offers(accepted_by);
CREATE INDEX idx_offers_store_id ON offers(store_id);
CREATE INDEX idx_offers_created_at ON offers(created_at);

CREATE TABLE offer_messages (
    id BIGSERIAL PRIMARY KEY,
    offer_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    content TEXT,
    image_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0,
    FOREIGN KEY (offer_id) REFERENCES offers(id),
    FOREIGN KEY (sender_id) REFERENCES users(id)
);

CREATE INDEX idx_offer_messages_offer_id ON offer_messages(offer_id);
CREATE INDEX idx_offer_messages_sender_id ON offer_messages(sender_id);
CREATE INDEX idx_offer_messages_created_at ON offer_messages(created_at);

CREATE TABLE system_notifications (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    created_by BIGINT NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE INDEX idx_system_notifications_created_at ON system_notifications(created_at);

CREATE TABLE subscription_payments (
    id BIGSERIAL PRIMARY KEY,
    store_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    period_start TIMESTAMP,
    period_end TIMESTAMP,
    paid_at TIMESTAMP,
    method VARCHAR(100),
    reference VARCHAR(255),
    notes TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0,
    FOREIGN KEY (store_id) REFERENCES stores(id)
);

CREATE INDEX idx_subscription_payments_store_id ON subscription_payments(store_id);
CREATE INDEX idx_subscription_payments_paid_at ON subscription_payments(paid_at);
CREATE INDEX idx_subscription_payments_created_at ON subscription_payments(created_at);
