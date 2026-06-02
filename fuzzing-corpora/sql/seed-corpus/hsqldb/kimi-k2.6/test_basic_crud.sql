-- Test: Basic CREATE, READ, UPDATE, DELETE operations
-- Run: java -jar sqltool.jar --rcFile sqltool.rc localhsql test_basic_crud.sql

CREATE TABLE test_users (
    user_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- CREATE (INSERT)
INSERT INTO test_users (username, email) VALUES ('alice', 'alice@example.com');
INSERT INTO test_users (username, email, is_active) VALUES ('bob', 'bob@example.com', TRUE);
INSERT INTO test_users (username, email, is_active) VALUES ('charlie', 'charlie@example.com', FALSE);
INSERT INTO test_users (username, email) VALUES ('diana', NULL);

-- READ (SELECT)
SELECT * FROM test_users;

SELECT user_id, username FROM test_users WHERE is_active = TRUE;

SELECT * FROM test_users WHERE email IS NOT NULL;

SELECT * FROM test_users WHERE email IS NULL;

-- UPDATE
UPDATE test_users SET email = 'diana@example.com' WHERE user_id = 4;
UPDATE test_users SET is_active = FALSE WHERE username = 'bob';

SELECT * FROM test_users ORDER BY user_id;

-- DELETE
DELETE FROM test_users WHERE username = 'charlie';

SELECT * FROM test_users ORDER BY user_id;

-- Test LIMIT/OFFSET (HSQLDB syntax)
SELECT * FROM test_users LIMIT 2 OFFSET 0;

-- Cleanup
DROP TABLE test_users CASCADE;