-- Insert sample data into groups
INSERT INTO groups (name, description)
VALUES ('Electronics', 'Devices and gadgets such as phones, laptops, etc.'),
       ('Groceries', 'Daily needs such as fruits, vegetables, etc.'),
       ('Clothing', 'Apparel items such as shirts, pants, etc.'),
       ('Furniture', 'Home and office furniture such as chairs, tables, etc.'),
       ('Books', 'All kinds of books and magazines.');

-- Insert sample data into items
INSERT INTO items (name, description, amount, cost, producer, group_id)
VALUES ('iPhone 14', 'Latest Apple smartphone', 50, 999.99, 'Apple Inc.', 1),
       ('Samsung Galaxy S21', 'Latest Samsung smartphone', 30, 799.99, 'Samsung Electronics', 1),
       ('Bananas', 'Fresh organic bananas', 200, 0.29, 'Local Farm', 2),
       ('Carrots', 'Fresh organic carrots', 150, 0.19, 'Local Farm', 2),
       ('T-Shirt', 'Cotton T-shirt, various colors', 100, 9.99, 'Fashion Brand', 3),
       ('Jeans', 'Denim jeans, various sizes', 75, 29.99, 'Fashion Brand', 3),
       ('Office Chair', 'Ergonomic office chair', 40, 149.99, 'Furniture Co.', 4),
       ('Dining Table', 'Wooden dining table, seats 6', 20, 399.99, 'Furniture Co.', 4),
       ('Harry Potter Book', 'Fantasy novel by J.K. Rowling', 100, 19.99, 'Bloomsbury Publishing', 5),
       ('Cooking Magazine', 'Monthly cooking recipes', 60, 4.99, 'Magazine Co.', 5);

-- Insert sample data into users
INSERT INTO users (login, password)
VALUES ('admin', 'password123'),
       ('john_doe', 'jd_password');