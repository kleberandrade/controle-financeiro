CREATE TABLE IF NOT EXISTS categories (name VARCHAR(60) PRIMARY KEY);
CREATE SEQUENCE IF NOT EXISTS entries_seq START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS entries (
 id BIGINT PRIMARY KEY,
 description VARCHAR(160) NOT NULL,
 kind VARCHAR(7) NOT NULL CHECK (kind IN ('RECEITA','DESPESA')),
 amount DECIMAL(14,2) NOT NULL CHECK (amount > 0),
 category VARCHAR(60) NOT NULL REFERENCES categories(name),
 entry_date DATE NOT NULL
);
CREATE INDEX IF NOT EXISTS entries_date_idx ON entries(entry_date);
CREATE TABLE IF NOT EXISTS budgets (month_key VARCHAR(7) PRIMARY KEY, amount DECIMAL(14,2) NOT NULL CHECK (amount >= 0));

MERGE INTO categories (name) KEY(name) VALUES ('Casa'),('Alimentação'),('Transporte'),('Lazer'),('Salário'),('Outros');
