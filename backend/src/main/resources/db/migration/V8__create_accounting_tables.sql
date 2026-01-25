-- V8__create_accounting_tables.sql
-- Create accounting tables: accounts, journal entries, supplier invoices, expenses

CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    store_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    number VARCHAR(50) NOT NULL,
    type VARCHAR(20) NOT NULL,
    sub_type VARCHAR(50),
    balance DECIMAL(12,2) NOT NULL DEFAULT 0,
    is_debit_normal BOOLEAN NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_accounts_store_id ON accounts(store_id);
CREATE UNIQUE INDEX uidx_accounts_store_number ON accounts(store_id, number);
CREATE UNIQUE INDEX uidx_accounts_store_sub_type ON accounts(store_id, sub_type) WHERE sub_type IS NOT NULL;

CREATE TABLE journal_entries (
    id BIGSERIAL PRIMARY KEY,
    store_id VARCHAR(255) NOT NULL,
    date TIMESTAMP NOT NULL,
    description VARCHAR(255) NOT NULL,
    source_type VARCHAR(20) NOT NULL,
    source_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_journal_entries_store_id_date ON journal_entries(store_id, date);
CREATE INDEX idx_journal_entries_source_type ON journal_entries(source_type);
CREATE INDEX idx_journal_entries_source_id ON journal_entries(source_id);

CREATE TABLE journal_entry_lines (
    id BIGSERIAL PRIMARY KEY,
    store_id VARCHAR(255) NOT NULL,
    journal_entry_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    type VARCHAR(10) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    account_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0,
    FOREIGN KEY (journal_entry_id) REFERENCES journal_entries(id),
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);

CREATE INDEX idx_journal_entry_lines_store_id_jeid ON journal_entry_lines(store_id, journal_entry_id);
CREATE INDEX idx_journal_entry_lines_account_id ON journal_entry_lines(account_id);

CREATE TABLE supplier_invoices (
    id BIGSERIAL PRIMARY KEY,
    store_id VARCHAR(255) NOT NULL,
    invoice_number VARCHAR(100) NOT NULL,
    supplier_id BIGINT NOT NULL,
    supplier_name VARCHAR(255) NOT NULL,
    purchase_order_id BIGINT NOT NULL,
    po_number VARCHAR(100) NOT NULL,
    invoice_date DATE NOT NULL,
    due_date DATE NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    amount_paid DECIMAL(10,2) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'UNPAID',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders(id)
);

CREATE INDEX idx_supplier_invoices_store_id ON supplier_invoices(store_id);
CREATE INDEX idx_supplier_invoices_supplier_id ON supplier_invoices(supplier_id);
CREATE INDEX idx_supplier_invoices_po_id ON supplier_invoices(purchase_order_id);
CREATE INDEX idx_supplier_invoices_invoice_number ON supplier_invoices(invoice_number);

CREATE TABLE supplier_payments (
    id BIGSERIAL PRIMARY KEY,
    store_id VARCHAR(255) NOT NULL,
    supplier_invoice_id BIGINT NOT NULL,
    date TIMESTAMP NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    method VARCHAR(100) NOT NULL,
    reference VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0,
    FOREIGN KEY (supplier_invoice_id) REFERENCES supplier_invoices(id)
);

CREATE INDEX idx_supplier_payments_store_id ON supplier_payments(store_id);
CREATE INDEX idx_supplier_payments_invoice_id ON supplier_payments(supplier_invoice_id);
CREATE INDEX idx_supplier_payments_date ON supplier_payments(date);

CREATE TABLE expenses (
    id BIGSERIAL PRIMARY KEY,
    store_id VARCHAR(255) NOT NULL,
    date TIMESTAMP NOT NULL,
    description VARCHAR(255) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    expense_account_id BIGINT NOT NULL,
    expense_account_name VARCHAR(255) NOT NULL,
    payment_account_id BIGINT NOT NULL,
    payment_account_name VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    reference VARCHAR(255),
    created_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    version BIGINT DEFAULT 0,
    FOREIGN KEY (expense_account_id) REFERENCES accounts(id),
    FOREIGN KEY (payment_account_id) REFERENCES accounts(id)
);

CREATE INDEX idx_expenses_store_id ON expenses(store_id);
CREATE INDEX idx_expenses_date ON expenses(date);
CREATE INDEX idx_expenses_expense_account_id ON expenses(expense_account_id);
