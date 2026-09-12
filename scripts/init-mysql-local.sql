CREATE DATABASE IF NOT EXISTS orders
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

CREATE DATABASE IF NOT EXISTS inventory
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

CREATE DATABASE IF NOT EXISTS payments
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

CREATE DATABASE IF NOT EXISTS notifications
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

CREATE USER IF NOT EXISTS 'commerce'@'localhost' IDENTIFIED BY 'commerce';
ALTER USER 'commerce'@'localhost' IDENTIFIED BY 'commerce';

GRANT ALL PRIVILEGES ON orders.* TO 'commerce'@'localhost';
GRANT ALL PRIVILEGES ON inventory.* TO 'commerce'@'localhost';
GRANT ALL PRIVILEGES ON payments.* TO 'commerce'@'localhost';
GRANT ALL PRIVILEGES ON notifications.* TO 'commerce'@'localhost';

FLUSH PRIVILEGES;
