-- Test: Primary keys, foreign keys, unique, check, not null constraints
CREATE TABLE test_departments (
    dept_id INTEGER PRIMARY KEY,
    dept_name VARCHAR(50)
);

CREATE TABLE test_employees (
    emp_id INTEGER PRIMARY KEY,
    emp_name VARCHAR(50),
    dept_id INTEGER,
    FOREIGN KEY (dept_id) REFERENCES test_departments(dept_id)
);

INSERT INTO test_departments VALUES (10, 'Sales');
INSERT INTO test_departments VALUES (20, 'Engineering');
INSERT INTO test_employees VALUES (1, 'Alice', 10);
INSERT INTO test_employees VALUES (2, 'Bob', 20);

-- Test ON DELETE CASCADE
CREATE TABLE test_employees_cascade (
                                        emp_id INTEGER PRIMARY KEY,
                                        emp_name VARCHAR(50),
                                        dept_id INTEGER,
                                        FOREIGN KEY (dept_id) REFERENCES test_departments(dept_id) ON DELETE CASCADE
);

INSERT INTO test_employees_cascade VALUES (10, 'David', 20);
INSERT INTO test_employees_cascade VALUES (11, 'Eve', 20);

SELECT * FROM test_employees_cascade;

-- Test CHECK constraints
CREATE TABLE test_check (
                            age INTEGER CHECK (age >= 0 AND age <= 150),
                            status VARCHAR(10) CHECK (status IN ('ACTIVE', 'INACTIVE', 'PENDING')),
                            salary NUMERIC(10,2) CHECK (salary > 0)
);

INSERT INTO test_check VALUES (25, 'ACTIVE', 50000.00);
INSERT INTO test_check VALUES (0, 'PENDING', 0.01);

SELECT * FROM test_check;

-- Test named constraints
CREATE TABLE test_named_constraints (
                                        id INTEGER CONSTRAINT pk_id PRIMARY KEY,
                                        email VARCHAR(100) CONSTRAINT uq_email UNIQUE,
                                        age INTEGER CONSTRAINT chk_age CHECK (age >= 18)
);

-- Cleanup
DROP TABLE test_employees CASCADE;
DROP TABLE test_departments CASCADE;