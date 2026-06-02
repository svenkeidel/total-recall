-- Test: Index types, usage, and query optimization
-- Run: java -jar sqltool.jar --rcFile sqltool.rc localhsql test_indexes.sql

CREATE TABLE test_index_demo (
                                 id INTEGER PRIMARY KEY,
                                 code VARCHAR(20),
                                 name VARCHAR(100),
                                 status VARCHAR(10),
                                 created_date DATE,
                                 metadata CLOB(1K),
);

-- Insert sample data
INSERT INTO test_index_demo VALUES
    (1, 'A001', 'Alpha Project', 'ACTIVE', DATE '2023-01-15', 'Details for alpha');
INSERT INTO test_index_demo VALUES
    (2, 'B002', 'Beta Initiative', 'PENDING', DATE '2023-03-20', 'Beta details here');
INSERT INTO test_index_demo VALUES
    (3, 'C003', 'Gamma Research', 'ACTIVE', DATE '2023-06-10', 'Gamma information');
INSERT INTO test_index_demo VALUES
    (4, 'A002', 'Delta Program', 'CLOSED', DATE '2022-11-05', 'Delta is done');
INSERT INTO test_index_demo VALUES
    (5, 'B003', 'Epsilon Study', 'ACTIVE', DATE '2024-01-08', 'Epsilon ongoing');

-- Default: primary key creates automatic unique index
SELECT * FROM INFORMATION_SCHEMA.SYSTEM_INDEXINFO WHERE TABLE_NAME = 'TEST_INDEX_DEMO';

-- Unique index
CREATE UNIQUE INDEX idx_unique_code ON test_index_demo(code);

-- Non-unique index
CREATE INDEX idx_status ON test_index_demo(status);

-- Composite index
CREATE INDEX idx_name_status ON test_index_demo(name, status);

-- Function-based index (HSQLDB supports expression indexes)
-- CREATE INDEX idx_upper_name ON test_index_demo(UCASE(name));

-- Show all indexes
SELECT
    INDEX_NAME,
    COLUMN_NAME,
    ORDINAL_POSITION,
    NON_UNIQUE
FROM INFORMATION_SCHEMA.SYSTEM_INDEXINFO
WHERE TABLE_NAME = 'TEST_INDEX_DEMO'
ORDER BY INDEX_NAME, ORDINAL_POSITION;

-- Test index usage with EXPLAIN PLAN
EXPLAIN PLAN FOR SELECT * FROM test_index_demo WHERE id = 3;
EXPLAIN PLAN FOR SELECT * FROM test_index_demo WHERE code = 'A001';
EXPLAIN PLAN FOR SELECT * FROM test_index_demo WHERE status = 'ACTIVE';
EXPLAIN PLAN FOR SELECT * FROM test_index_demo WHERE UPPER(name) LIKE 'ALPHA%';

-- Test covering index
SELECT code, status FROM test_index_demo WHERE code = 'A001';

-- Range scan using index
SELECT * FROM test_index_demo WHERE id BETWEEN 2 AND 4 ORDER BY id;

-- Index with DESC order (HSQLDB 2.0)
CREATE INDEX idx_date_desc ON test_index_demo(created_date DESC);

-- Partial/WHERE clause index (HSQLDB 2.0 feature)
-- CREATE INDEX idx_active_only ON test_index_demo(name) WHERE status = 'ACTIVE';

-- Test index selectivity
SELECT status, COUNT(*) FROM test_index_demo GROUP BY status;

-- Test DROP INDEX
DROP INDEX idx_date_desc IF EXISTS;

-- Test table scan vs index access
-- In HSQLDB, use table hints or just observe plan
EXPLAIN PLAN FOR SELECT /*+ NO_INDEX */ * FROM test_index_demo WHERE status = 'ACTIVE';

-- Test HASH index (memory tables only, but syntax check)
-- CREATE HASH INDEX idx_hash_status ON test_index_demo(status);

-- Rebuild indexes (HSQLDB specific)
SELECT
    TABLE_NAME,
    TABLE_TYPE
FROM INFORMATION_SCHEMA.SYSTEM_TABLES
WHERE TABLE_NAME = 'TEST_INDEX_DEMO';

-- Cleanup
DROP INDEX idx_unique_code IF EXISTS;
DROP INDEX idx_status IF EXISTS;
DROP INDEX idx_name_status IF EXISTS;
DROP INDEX idx_upper_name IF EXISTS;
DROP INDEX idx_active_only IF EXISTS;
DROP TABLE test_index_demo CASCADE;