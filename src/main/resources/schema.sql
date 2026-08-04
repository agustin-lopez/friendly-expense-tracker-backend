--TABLES
CREATE TABLE IF NOT EXISTS users (
    id uuid PRIMARY KEY,
    name varchar(40) NOT NULL,
    email varchar(255) NOT NULL UNIQUE,
    password_hash varchar(255) NOT NULL,
    registration_date date NOT NULL,
    email_verified boolean NOT NULL DEFAULT false
);

CREATE TABLE IF NOT EXISTS categories (
    id uuid PRIMARY KEY,
    user_id uuid NOT NULL REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    name varchar(50) NOT NULL,
    type varchar(20) NOT NULL,
    icon varchar(50),
    CONSTRAINT CK_CATEGORY_TYPE CHECK (type = 'EXPENSE' OR type = 'INCOME'),
    CONSTRAINT UQ_CATEGORY_USER_NAME UNIQUE (user_id, name)
);

CREATE TABLE IF NOT EXISTS transactions (
    id uuid PRIMARY KEY,
    user_id uuid NOT NULL REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    category_id uuid REFERENCES categories (id) ON DELETE SET NULL ON UPDATE CASCADE,
    amount decimal(10,2) NOT NULL,
    description varchar(120),
    transaction_date date NOT NULL
);

CREATE TABLE IF NOT EXISTS temporary_tokens (
    id uuid PRIMARY KEY,
    user_id uuid NOT NULL REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    token varchar(255) NOT NULL UNIQUE,
    type varchar(30) NOT NULL,
    expires_at timestamp NOT NULL,
    used boolean NOT NULL DEFAULT false,
    CONSTRAINT CK_TOKEN_TYPE CHECK (type = 'PASSWORD_RESET' OR type = 'EMAIL_VERIFICATION')
);

CREATE INDEX IF NOT EXISTS idx_categories_user_id ON categories (user_id);
CREATE INDEX IF NOT EXISTS idx_transactions_user_id ON transactions (user_id);
CREATE INDEX IF NOT EXISTS idx_transactions_category_id ON transactions (category_id);
