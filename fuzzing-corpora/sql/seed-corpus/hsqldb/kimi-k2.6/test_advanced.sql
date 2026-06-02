-- Test: Window functions, MERGE, MATCH_RECOGNIZE, LATERAL, etc.
-- Run: java -jar sqltool.jar --rcFile sqltool.rc localhsql test_advanced.sql

CREATE TABLE test_sales (
                            sale_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                            product VARCHAR(50),
                            category VARCHAR(20),
                            sale_date DATE,
                            amount NUMERIC(10,2),
                            units INTEGER,
                            region VARCHAR(20)
);

INSERT INTO test_sales (product, category, sale_date, amount, units, region) VALUES
                                                                                 ('Widget', 'Electronics', DATE '2024-01-15', 199.99, 5, 'North'),
                                                                                 ('Gadget', 'Electronics', DATE '2024-01-20', 299.99, 3, 'South'),
                                                                                 ('Widget', 'Electronics', DATE '2024-02-10', 189.99, 8, 'North'),
                                                                                 ('Tool', 'Hardware', DATE '2024-02-15', 49.99, 12, 'East'),
                                                                                 ('Gadget', 'Electronics', DATE '2024-03-05', 279.99, 4, 'West'),
                                                                                 ('Widget', 'Electronics', DATE '2024-03-12', 199.99, 6, 'North'),
                                                                                 ('Supply', 'Hardware', DATE '2024-03-20', 9.99, 50, 'South'),
                                                                                 ('Widget', 'Electronics', DATE '2024-04-01', 179.99, 10, 'East');

-- ============================================
-- Window Functions (HSQLDB 2.0)
-- ============================================

SELECT
    product,
    amount,
    ROW_NUMBER() OVER (ORDER BY amount DESC) AS row_num,
    RANK() OVER (ORDER BY amount DESC) AS rank_num,
    DENSE_RANK() OVER (ORDER BY amount DESC) AS dense_rank_num
FROM test_sales;

SELECT
    product,
    category,
    amount,
    ROW_NUMBER() OVER (PARTITION BY category ORDER BY amount DESC) AS cat_rank,
    SUM(amount) OVER (PARTITION BY category) AS cat_total,
    amount / SUM(amount) OVER (PARTITION BY category) AS cat_pct
FROM test_sales
ORDER BY category, cat_rank;

SELECT
    sale_id,
    sale_date,
    amount,
    SUM(amount) OVER (ORDER BY sale_date ROWS UNBOUNDED PRECEDING) AS running_total,
    AVG(amount) OVER (ORDER BY sale_date ROWS BETWEEN 2 PRECEDING AND CURRENT ROW) AS moving_avg_3,
    LAG(amount, 1) OVER (ORDER BY sale_date) AS prev_amount,
    LEAD(amount, 1) OVER (ORDER BY sale_date) AS next_amount,
    amount - LAG(amount, 1) OVER (ORDER BY sale_date) AS change
FROM test_sales ORDER BY sale_date;

SELECT
    product,
    amount,
    FIRST_VALUE(amount) OVER (ORDER BY amount) AS lowest,
    LAST_VALUE(amount) OVER (ORDER BY amount
        ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) AS highest,
    NTH_VALUE(amount, 2) OVER (ORDER BY amount
        ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) AS second_lowest
FROM test_sales;

-- ============================================
-- MERGE Statement (SQL:2003, HSQLDB 2.0)
-- ============================================

CREATE TABLE test_product_inventory (
                                        product VARCHAR(50) PRIMARY KEY,
                                        stock INTEGER,
                                        last_updated DATE
);

INSERT INTO test_product_inventory VALUES ('Widget', 100, DATE '2024-01-01');
INSERT INTO test_product_inventory VALUES ('Gadget', 50, DATE '2024-01-01');

CREATE TABLE test_sales_update (
                                   product VARCHAR(50),
                                   sold INTEGER,
                                   restocked INTEGER
);

INSERT INTO test_sales_update VALUES ('Widget', 30, 0);
INSERT INTO test_sales_update VALUES ('Gadget', 10, 20);
INSERT INTO test_sales_update VALUES ('Tool', 0, 100);  -- New product

SELECT * FROM test_product_inventory ORDER BY product;

MERGE INTO test_product_inventory AS target
    USING test_sales_update AS source
    ON target.product = source.product
    WHEN MATCHED THEN
        UPDATE SET
            stock = target.stock - source.sold + source.restocked,
            last_updated = CURRENT_DATE
    WHEN NOT MATCHED THEN
        INSERT (product, stock, last_updated)
            VALUES (source.product, source.restocked, CURRENT_DATE);

SELECT * FROM test_product_inventory ORDER BY product;

-- ============================================
-- LATERAL Derived Tables (HSQLDB 2.0)
-- ============================================

CREATE TABLE test_departments_lr (
                                     dept_id INTEGER PRIMARY KEY,
                                     dept_name VARCHAR(50)
);

INSERT INTO test_departments_lr VALUES (1, 'Sales'), (2, 'Engineering');

CREATE TABLE test_employees_lr (
                                   emp_id INTEGER PRIMARY KEY,
                                   emp_name VARCHAR(50),
                                   dept_id INTEGER,
                                   salary NUMERIC(10,2)
);

INSERT INTO test_employees_lr VALUES
                                  (1, 'Alice', 1, 80000), (2, 'Bob', 1, 90000), (3, 'Carol', 1, 85000),
                                  (4, 'David', 2, 95000), (5, 'Eve', 2, 100000);

SELECT d.dept_name, top_earners.emp_name, top_earners.salary
FROM test_departments_lr d,
     LATERAL (
              SELECT emp_name, salary
              FROM test_employees_lr e
              WHERE e.dept_id = d.dept_id
              ORDER BY salary DESC
              LIMIT 2
) AS top_earners;

-- ============================================
-- FULL JOIN, NATURAL JOIN, CROSS JOIN
-- ============================================

CREATE TABLE test_a (id INTEGER, val_a VARCHAR(10));
CREATE TABLE test_b (id INTEGER, val_b VARCHAR(10));

INSERT INTO test_a VALUES (1, 'a1'), (2, 'a2'), (3, 'a3');
INSERT INTO test_b VALUES (1, 'b1'), (2, 'b2'), (4, 'b4');

SELECT * FROM test_a INNER JOIN test_b ON test_a.id = test_b.id;

SELECT * FROM test_a LEFT JOIN test_b ON test_a.id = test_b.id;

SELECT * FROM test_a RIGHT JOIN test_b ON test_a.id = test_b.id;

SELECT COALESCE(a.id, b.id) AS id, a.val_a, b.val_b
FROM test_a a FULL JOIN test_b b ON a.id = b.id;

SELECT * FROM test_a CROSS JOIN test_b;

SELECT * FROM test_a NATURAL JOIN test_b;

-- ============================================
-- GROUP BY extensions: ROLLUP, CUBE, GROUPING SETS
-- ============================================

SELECT
    COALESCE(category, 'ALL CATEGORIES') AS category,
    COALESCE(region, 'ALL REGIONS') AS region,
    SUM(amount) AS total_sales,
    COUNT(*) AS num_sales
FROM test_sales
GROUP BY ROLLUP(category, region)
ORDER BY category, region;

SELECT
    COALESCE(category, 'ALL') AS category,
    COALESCE(region, 'ALL') AS region,
    SUM(amount) AS total_sales
FROM test_sales
GROUP BY CUBE(category, region)
ORDER BY GROUPING(category), GROUPING(region), category, region;

SELECT
    COALESCE(category, 'ALL') AS category,
    COALESCE(region, 'ALL') AS region,
    SUM(amount) AS total_sales
FROM test_sales
GROUP BY GROUPING SETS (
    (category, region),
    (category),
    (region),
    ()
    )
ORDER BY GROUPING(category), GROUPING(region);

-- ============================================
-- Table value constructors
-- ============================================

SELECT * FROM (VALUES
                   (1, 'one'),
                   (2, 'two'),
                   (3, 'three')
              ) AS t(num, word)
WHERE num > 1;

-- ============================================
-- Cleanup
-- ============================================

DROP TABLE test_a CASCADE;
DROP TABLE test_b CASCADE;
DROP TABLE test_sales_update CASCADE;
DROP TABLE test_product_inventory CASCADE;
DROP TABLE test_employees_lr CASCADE;
DROP TABLE test_departments_lr CASCADE;
DROP TABLE test_sales CASCADE;