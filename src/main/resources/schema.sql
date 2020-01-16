DROP TABLE IF EXISTS `accounts`;
CREATE TABLE accounts (
    id BIGINT PRIMARY KEY NOT NULL,
    name VARCHAR(100) UNIQUE NOT NULL,
    balance VARCHAR(255) NOT NULL,
    currency VARCHAR(3) NOT NULL
);

DROP TABLE IF EXISTS `conversions`;
CREATE TABLE conversion_rate (
    id INT NOT NULL,
    currency VARCHAR(3) UNIQUE NOT NULL,
    exchange_rate_to_eur VARCHAR(255) NOT NULL
);