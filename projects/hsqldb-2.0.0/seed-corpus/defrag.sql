-- Create a table for Customers
CREATE TABLE Customers (
   CustomerID INT PRIMARY KEY,
   FirstName VARCHAR(50) NOT NULL,
   LastName VARCHAR(50) NOT NULL,
   Email VARCHAR(100) UNIQUE,
   JoinDate DATE DEFAULT CURRENT_DATE
);

-- Create a table for Products
CREATE TABLE Products (
   ProductID INT PRIMARY KEY,
   ProductName VARCHAR(100) NOT NULL,
   Category VARCHAR(50),
   Price DECIMAL(10, 2) CHECK (Price > 0),
   StockQuantity INT DEFAULT 0
);

-- Create an Orders table with Foreign Keys linking to Customers and Products
CREATE TABLE Orders (
   OrderID INT PRIMARY KEY,
   CustomerID INT,
   ProductID INT,
   OrderDate DATE DEFAULT CURRENT_DATE,
   Quantity INT CHECK (Quantity > 0),
   FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID),
   FOREIGN KEY (ProductID) REFERENCES Products(ProductID)
);

-- Insert mock customers
INSERT INTO Customers (CustomerID, FirstName, LastName, Email, JoinDate) VALUES
    (101, 'John', 'Doe', 'john.doe@email.com', '2025-01-15'),
    (102, 'Jane', 'Smith', 'jane.smith@email.com', '2025-02-20'),
    (103, 'Alice', 'Johnson', 'alice.j@email.com', '2025-03-05');

-- Insert mock products
INSERT INTO Products (ProductID, ProductName, Category, Price, StockQuantity) VALUES
    (1, 'Wireless Mouse', 'Electronics', 25.99, 120),
    (2, 'Mechanical Keyboard', 'Electronics', 89.99, 45),
    (3, 'Ergonomic Office Chair', 'Furniture', 199.50, 15),
    (4, 'Water Bottle 1L', 'Accessories', 14.95, 200);

-- Insert mock orders linking the tables together
INSERT INTO Orders (OrderID, CustomerID, ProductID, OrderDate, Quantity) VALUES
    (1, 101, 1, '2026-05-10', 2), -- John Doe bought 2 Wireless Mice
    (2, 102, 3, '2026-05-12', 1), -- Jane Smith bought 1 Office Chair
    (3, 103, 2, '2026-05-15', 1), -- Alice Johnson bought 1 Keyboard
    (4, 101, 4, '2026-05-16', 3); -- John Doe bought 3 Water Bottles

SELECT
    o.OrderID,
    p.ProductName,
    o.Quantity,
    (o.Quantity * p.Price) AS TotalPrice,
    o.OrderDate
FROM Orders o
         JOIN Customers c ON o.CustomerID = c.CustomerID
         JOIN Products p ON o.ProductID = p.ProductID;

CHECKPOINT DEFRAG;