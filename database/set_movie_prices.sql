ALTER TABLE movies ADD price FLOAT NOT NULL;
UPDATE movies SET price = ROUND(RAND(5)*9 + 1,2);
