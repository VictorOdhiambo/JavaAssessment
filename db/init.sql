-- Enable UUID generation
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ==============================================================
-- 1. CUSTOMERS TABLE
-- ==============================================================

CREATE TABLE customers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    identity_number VARCHAR(50) NOT NULL UNIQUE,
    phone_number VARCHAR(20) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    dob DATE NOT NULL,

    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status INT DEFAULT 0,  -- 0 = PENDING_VERIFICATION, 1 = ACTIVE
    verification_code VARCHAR(100),
    hash_password VARCHAR(255)
);

-- Index for faster lookups
CREATE INDEX idx_customers_email ON customers(email);

-- ==============================================================
-- 2. ACCOUNTS TABLE
-- ==============================================================

CREATE TABLE accounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    customer_id UUID NOT NULL,
    account_number BIGINT NOT NULL UNIQUE,
    currency VARCHAR(10) NOT NULL DEFAULT 'KES',
    balance NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    status INT DEFAULT 0,  -- 0 = INACTIVE, 1 = ACTIVE
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_accounts_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers (id)
        ON DELETE CASCADE
);

CREATE INDEX idx_accounts_customer_id ON accounts(customer_id);

-- ==============================================================
-- 3. LOANS TABLE
-- ==============================================================

CREATE TABLE loans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    account_id UUID NOT NULL,
    amount NUMERIC(15,2) NOT NULL,
    tenure INT NOT NULL,  -- in months
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_loans_account
        FOREIGN KEY (account_id)
        REFERENCES accounts (id)
        ON DELETE CASCADE
);

CREATE INDEX idx_loans_account_id ON loans(account_id);
