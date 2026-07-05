--TABLES
CREATE TABLE IF NOT EXISTS users (
    id uuid NOT NULL, --PK
    name varchar(100) NOT NULL,
    email varchar(255) UNIQUE NOT NULL,
    password_hash varchar(255) NOT NULL,
    registration_date timestamp NOT NULL
);

CREATE TABLE IF NOT EXISTS categories (
    id uuid NOT NULL, --PK
    name varchar(20) NOT NULL,
    type varchar(20) NOT NULL --EXPENSE/INCONME
);

CREATE TABLE IF NOT EXISTS transactions (
    id uuid NOT NULL, --PK
    user_id uuid NOT NULL, --FK
    category_id uuid NOT NULL, --FK
    amount decimal(10,2) NOT NULL,
    description varchar(120),
    transaction_date date NOT NULL
);

--PRIMARY KEYS
ALTER TABLE users
ADD CONSTRAINT PK_USER
PRIMARY KEY (id);

ALTER TABLE categories
ADD CONSTRAINT PK_CATEGORY
PRIMARY KEY (id);

ALTER TABLE transactions
ADD CONSTRAINT PK_TRANSACTION
PRIMARY KEY (id);

--FOREIGN KEYS
ALTER TABLE transactions
ADD CONSTRAINT FK_TRANSACTION_USER_ID
FOREIGN KEY (user_id)
REFERENCES users (id);

ALTER TABLE transactions
ADD CONSTRAINT FK_TRANSACTION_CATEGORY_ID
FOREIGN KEY (category_id)
REFERENCES categories (id);

--ADDITIONAL CONSTRAINTS
ALTER TABLE categories
ADD CONSTRAINT CK_CATEGORY_TYPE
CHECK (
    type = 'EXPENSE' OR type = 'INCOME'
);
