-- Test: Transaction control, savepoints, isolation levels
-- Run: java -jar sqltool.jar --rcFile sqltool.rc localhsql test_transactions.sql

CREATE TABLE test_accounts (
                               account_id INTEGER PRIMARY KEY,
                               balance NUMERIC(10,2),
                               version INTEGER DEFAULT 0
);

INSERT INTO test_accounts VALUES (1, 1000.00, 0);
INSERT INTO test_accounts VALUES (2, 500.00, 0);

-- Test basic transaction COMMIT
SELECT '=== Test COMMIT ===' AS test_case FROM DUAL;
START TRANSACTION;
UPDATE test_accounts SET balance = balance - 100 WHERE account_id = 1;
UPDATE test_accounts SET balance = balance + 100 WHERE account_id = 2;
COMMIT;

SELECT * FROM test_accounts;

-- Test ROLLBACK
SELECT '=== Test ROLLBACK ===' AS test_case FROM DUAL;
START TRANSACTION;
UPDATE test_accounts SET balance = 0;  -- Bad update
ROLLBACK;

SELECT 'Balances should be unchanged:' AS note, * FROM test_accounts;

-- Test SAVEPOINTS
SELECT '=== Test SAVEPOINTS ===' AS test_case FROM DUAL;
START TRANSACTION;
UPDATE test_accounts SET balance = balance - 200 WHERE account_id = 1;
SAVEPOINT before_transfer;

UPDATE test_accounts SET balance = balance + 200 WHERE account_id = 2;
-- Oops, wrong amount! Rollback to savepoint
ROLLBACK TO SAVEPOINT before_transfer;

-- Correct transfer
UPDATE test_accounts SET balance = balance + 150 WHERE account_id = 2;
UPDATE test_accounts SET balance = balance - 50 WHERE account_id = 1;  -- Adjust first account

COMMIT;

SELECT 'After savepoint rollback:' AS note, * FROM test_accounts;

-- Test READ ONLY transaction
SELECT '=== Test READ ONLY transaction ===' AS test_case FROM DUAL;
SET TRANSACTION READ ONLY;
SELECT * FROM test_accounts;
-- INSERT would fail here in READ ONLY mode
SET TRANSACTION READ WRITE;

-- Test isolation level settings
SELECT '=== Test isolation levels ===' AS test_case FROM DUAL;
SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;

-- Default isolation restored
SELECT CURRENT_TIMESTAMP AS now FROM DUAL;

-- Test LOCK TABLE
SELECT '=== Test LOCK TABLE ===' AS test_case FROM DUAL;
LOCK TABLE test_accounts IN EXCLUSIVE MODE;
UPDATE test_accounts SET version = version + 1;
COMMIT;

-- Test optimistic locking with version column
SELECT '=== Test optimistic locking pattern ===' AS test_case FROM DUAL;
START TRANSACTION;
UPDATE test_accounts
SET balance = balance - 100, version = version + 1
WHERE account_id = 1 AND version = (SELECT version FROM test_accounts WHERE account_id = 1);
COMMIT;

SELECT * FROM test_accounts;

-- Test two-phase commit simulation (prepare/ignore unsupported, demonstrate pattern)
SELECT '=== Transaction metadata ===' AS test_case FROM DUAL;
SELECT
    TRANSACTION_ID() AS tx_id,
    AUTOCOMMIT() AS autocommit_status
FROM DUAL;

-- Cleanup
DROP TABLE test_accounts CASCADE;