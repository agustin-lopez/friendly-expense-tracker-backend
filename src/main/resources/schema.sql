--TABLES
CREATE TABLE IF NOT EXISTS users (
    id uuid PRIMARY KEY,
    name varchar(100) NOT NULL,
    email varchar(255) NOT NULL UNIQUE,
    password_hash varchar(255) NOT NULL,
    registration_date date NOT NULL
);

CREATE TABLE IF NOT EXISTS categories (
    id uuid PRIMARY KEY,
    name varchar(50) UNIQUE NOT NULL,
    type varchar(20) NOT NULL,

    CONSTRAINT CK_CATEGORY_TYPE CHECK (type = 'EXPENSE' OR type = 'INCOME')
);

CREATE TABLE IF NOT EXISTS transactions (
    id uuid PRIMARY KEY,
    user_id uuid NOT NULL REFERENCES users (id),
    category_id uuid NOT NULL REFERENCES categories (id),
    amount decimal(10,2) NOT NULL,
    description varchar(120),
    transaction_date date NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_transactions_user_id ON transactions (user_id);
CREATE INDEX IF NOT EXISTS idx_transactions_category_id ON transactions (category_id);