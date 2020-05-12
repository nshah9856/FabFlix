USE moviedb;

CREATE TABLE employees (
    email VARCHAR(50) DEFAULT '' PRIMARY KEY,
    password VARCHAR(20) DEFAULT '' NOT NULL,
    fullname VARCHAR(100) DEFAULT ''
);