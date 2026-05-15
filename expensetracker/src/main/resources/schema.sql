-- Schema for Expense Tracker API

CREATE DATABASE IF NOT EXISTS expensetracker;
USE expensetracker;

-- Users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Categories table
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    monthly_budget DECIMAL(10,2) DEFAULT NULL,
    CONSTRAINT fk_category_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT uq_user_category_name UNIQUE (user_id, name)
);

-- Expenses table
CREATE TABLE expenses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    description VARCHAR(255) NOT NULL,
    expense_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_expense_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_expense_category FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT chk_amount_positive CHECK (amount > 0)
);

-- Sample Data
INSERT INTO users (name, email) VALUES
('Richa Tripathi', 'richa@example.com'),
('John Doe', 'john@example.com');

INSERT INTO categories (user_id, name, monthly_budget) VALUES
(1, 'Food', 5000.00),
(1, 'Travel', 3000.00),
(1, 'Bills', 10000.00),
(2, 'Entertainment', 2000.00),
(2, 'Food', NULL);

INSERT INTO expenses (user_id, category_id, amount, description, expense_date) VALUES
(1, 1, 250.00, 'Lunch at restaurant', '2026-05-01'),
(1, 1, 150.00, 'Groceries from store', '2026-05-03'),
(1, 2, 1200.00, 'Cab to airport', '2026-05-05'),
(1, 3, 2500.00, 'Electricity bill payment', '2026-05-10'),
(2, 4, 500.00, 'Movie tickets', '2026-05-08');
