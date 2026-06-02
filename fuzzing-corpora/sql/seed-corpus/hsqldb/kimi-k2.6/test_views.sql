-- Test: Views, materialized views, recursive views, WITH clause
AVG(e.salary) AS avg_salary,
    MAX(e.salary) AS max_salary,
    MIN(e.salary) AS min_salary
FROM test_employees e
JOIN test_departments d ON e.dept_id = d.dept_id
GROUP BY d.dept_name;

SELECT '=== View with aggregation ===' AS test_case FROM DUAL;
SELECT * FROM v_dept_salary ORDER BY dept_name;

-- Test WITH clause (CTEs) - HSQLDB 2.0
SELECT '=== CTE (WITH clause) ===' AS test_case FROM DUAL;
WITH dept_stats AS (
    SELECT dept_id, AVG(salary) AS avg_sal FROM test_employees GROUP BY dept_id
)
SELECT e.emp_name, e.salary, ds.avg_sal,
       CASE WHEN e.salary > ds.avg_sal THEN 'Above average' ELSE 'Below average' END AS comparison
FROM test_employees e
         JOIN dept_stats ds ON e.dept_id = ds.dept_id
ORDER BY e.emp_name;

-- Recursive CTE for org chart (HSQLDB 2.0)
SELECT '=== Recursive CTE: org chart ===' AS test_case FROM DUAL;
WITH RECURSIVE org_chart(level, emp_id, emp_name, manager_id, path) AS (
    -- Anchor: top-level
    SELECT 1, emp_id, emp_name, manager_id, CAST(emp_name AS VARCHAR(200))
    FROM test_employees WHERE manager_id IS NULL

    UNION ALL

    -- Recursive: employees with managers
    SELECT
        oc.level + 1,
        e.emp_id,
        e.emp_name,
        e.manager_id,
        oc.path || ' -> ' || e.emp_name
    FROM org_chart oc
             JOIN test_employees e ON e.manager_id = oc.emp_id
)
SELECT level, REPEAT('  ', level - 1) || emp_name AS indented_name, path
FROM org_chart
ORDER BY path;

-- Test updatable view
CREATE VIEW v_sales_team AS
SELECT emp_id, emp_name, salary
FROM test_employees
WHERE dept_id = 2
WITH CHECK OPTION;  -- Prevents updates that would exclude row from view

SELECT '=== Updatable view ===' AS test_case FROM DUAL;
SELECT * FROM v_sales_team;

UPDATE v_sales_team SET salary = salary * 1.10 WHERE emp_id = 4;
SELECT 'After 10% raise:' AS note, * FROM v_sales_team WHERE emp_id = 4;

-- This should fail due to WITH CHECK OPTION
SELECT '=== Testing WITH CHECK OPTION (expect error) ===' AS test_case FROM DUAL;
UPDATE v_sales_team SET salary = 500000 WHERE emp_id = 4;  -- Would still qualify, actually OK
-- Better test: create view that would be violated
CREATE VIEW v_low_paid AS
SELECT emp_id, emp_name, salary FROM test_employees WHERE salary < 100000
WITH CHECK OPTION;

UPDATE v_low_paid SET salary = 200000 WHERE emp_id = 4;  -- ERROR! Excludes from view

-- Test MATERIALIZED VIEW (HSQLDB 2.0)
CREATE TABLE test_sales_log (
                                sale_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                amount NUMERIC(10,2),
                                sale_date DATE
);

INSERT INTO test_sales_log (amount, sale_date) VALUES
                                                   (100.00, DATE '2024-01-15'),
                                                   (200.00, DATE '2024-01-16'),
                                                   (150.00, DATE '2024-01-17');

-- Materialized view snapshot
CREATE TABLE mv_daily_sales AS
SELECT sale_date, SUM(amount) AS daily_total, COUNT(*) AS sale_count
FROM test_sales_log
GROUP BY sale_date
    WITH DATA;  -- HSQLDB: use CREATE TABLE AS SELECT for materialized view pattern

SELECT '=== Materialized view pattern ===' AS test_case FROM DUAL;
SELECT * FROM mv_daily_sales ORDER BY sale_date;

-- Refresh pattern
TRUNCATE TABLE mv_daily_sales;
INSERT INTO mv_daily_sales
SELECT sale_date, SUM(amount) AS daily_total, COUNT(*) AS sale_count
FROM test_sales_log
GROUP BY sale_date;

-- Cleanup
DROP VIEW v_high_earners CASCADE;
DROP VIEW v_emp_dept CASCADE;
DROP VIEW v_dept_salary CASCADE;
DROP VIEW v_sales_team CASCADE;
DROP VIEW v_low_paid CASCADE;
DROP TABLE mv_daily_sales CASCADE;
DROP TABLE test_sales_log CASCADE;
DROP TABLE test_employees CASCADE;
DROP TABLE test_departments CASCADE;