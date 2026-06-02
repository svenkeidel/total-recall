-- Test: System tables, information schema, metadata queries
-- Run: java -jar sqltool.jar --rcFile sqltool.rc localhsql test_metadata.sql

-- Create diverse schema objects for metadata inspection
CREATE SCHEMA test_schema AUTHORIZATION DBA;

SET SCHEMA test_schema;

CREATE TABLE test_metadata_table (
                                     id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                     code VARCHAR(20) NOT NULL UNIQUE,
                                     name VARCHAR(100),
                                     description CLOB(1K),
                                     status VARCHAR(10) DEFAULT 'ACTIVE',
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     ranking DECIMAL(5,2),
                                     flags BINARY(4),
                                     data BLOB(1K),
                                     settings VARBINARY(100)
);

CREATE INDEX idx_meta_name ON test_metadata_table(name);
CREATE INDEX idx_meta_status_rank ON test_metadata_table(status, ranking DESC);

COMMENT ON TABLE test_metadata_table IS 'Main table for metadata testing';
COMMENT ON COLUMN test_metadata_table.id IS 'Primary key';
COMMENT ON COLUMN test_metadata_table.code IS 'Unique business code';

CREATE VIEW test_metadata_view AS
SELECT id, code, name, status FROM test_metadata_table WHERE status = 'ACTIVE';

CREATE SEQUENCE test_metadata_seq
    START WITH 1000
    INCREMENT BY 10;


-- Switch back to default for cross-schema queries
SET SCHEMA PUBLIC;
--
-- ============================================
-- INFORMATION_SCHEMA queries
-- ============================================

SELECT SCHEMA_NAME, SCHEMA_OWNER FROM INFORMATION_SCHEMA.SCHEMATA;

SELECT
    TABLE_SCHEMA,
    TABLE_NAME,
    TABLE_TYPE,
    SELF_REFERENCING_COLUMN_NAME,
    REFERENCE_GENERATION
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_SCHEMA = 'TEST_SCHEMA';

SELECT
    TABLE_NAME,
    COLUMN_NAME,
    DATA_TYPE,
    CHARACTER_MAXIMUM_LENGTH,
    NUMERIC_PRECISION,
    NUMERIC_SCALE,
    IS_NULLABLE,
    COLUMN_DEFAULT,
    IS_IDENTITY,
    IDENTITY_GENERATION,
    IDENTITY_START,
    IDENTITY_INCREMENT
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'TEST_SCHEMA' AND TABLE_NAME = 'TEST_METADATA_TABLE'
ORDER BY ORDINAL_POSITION;

SELECT
    CONSTRAINT_SCHEMA,
    CONSTRAINT_NAME,
    CONSTRAINT_TYPE,
    TABLE_NAME,
    IS_DEFERRABLE,
    INITIALLY_DEFERRED
FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
WHERE TABLE_SCHEMA = 'TEST_SCHEMA';

SELECT
    CONSTRAINT_NAME,
    TABLE_NAME,
    COLUMN_NAME,
    ORDINAL_POSITION
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'TEST_SCHEMA';

SELECT * FROM INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS WHERE CONSTRAINT_SCHEMA = 'TEST_SCHEMA';

SELECT * FROM INFORMATION_SCHEMA.CHECK_CONSTRAINTS WHERE CONSTRAINT_SCHEMA = 'TEST_SCHEMA';

SELECT
    TABLE_NAME,
    INDEX_NAME,
    INDEX_QUALIFIER,
    ORDINAL_POSITION,
    COLUMN_NAME,
    ASC_OR_DESC
FROM INFORMATION_SCHEMA.SYSTEM_INDEXINFO
ORDER BY TABLE_NAME, INDEX_NAME, ORDINAL_POSITION;

-- HSQLDB-specific system tables
SELECT
    TABLE_SCHEMA,
    TABLE_NAME,
    VIEW_DEFINITION
FROM INFORMATION_SCHEMA.VIEWS
WHERE TABLE_SCHEMA = 'TEST_SCHEMA';

SELECT
    SEQUENCE_SCHEMA,
    SEQUENCE_NAME,
    DATA_TYPE,
    NUMERIC_PRECISION,
    MINIMUM_VALUE,
    MAXIMUM_VALUE,
    INCREMENT,
    CYCLE_OPTION
FROM INFORMATION_SCHEMA.SEQUENCES
WHERE SEQUENCE_SCHEMA = 'TEST_SCHEMA';

SELECT
    ROUTINE_SCHEMA,
    ROUTINE_NAME,
    ROUTINE_TYPE,
    DATA_TYPE,
    ROUTINE_BODY,
    ROUTINE_DEFINITION
FROM INFORMATION_SCHEMA.ROUTINES
WHERE ROUTINE_SCHEMA = 'TEST_SCHEMA';

SELECT
    SPECIFIC_NAME,
    PARAMETER_NAME,
    PARAMETER_MODE,
    DATA_TYPE,
    PARAMETER_NAME
FROM INFORMATION_SCHEMA.PARAMETERS
WHERE SPECIFIC_SCHEMA = 'TEST_SCHEMA';

-- Comments
SELECT
    OBJECT_SCHEMA,
    OBJECT_NAME,
    OBJECT_TYPE
FROM INFORMATION_SCHEMA.SYSTEM_COMMENTS
WHERE OBJECT_SCHEMA = 'TEST_SCHEMA';

-- ============================================
-- Legacy HSQLDB system tables (for compatibility)
-- ============================================

SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES WHERE TABLE_SCHEM = 'TEST_SCHEMA';

SELECT
    TABLE_NAME,
    COLUMN_NAME,
    TYPE_NAME,
    COLUMN_SIZE,
    DECIMAL_DIGITS,
    NULLABLE,
    COLUMN_DEF,
    IS_NULLABLE
FROM INFORMATION_SCHEMA.SYSTEM_COLUMNS
WHERE TABLE_SCHEM = 'TEST_SCHEMA' AND TABLE_NAME = 'TEST_METADATA_TABLE';

-- ============================================
-- Database properties and settings
-- ============================================



-- ============================================
-- Cleanup
-- ============================================

SET SCHEMA test_schema;
DROP FUNCTION test_meta_func IF EXISTS;
DROP SEQUENCE test_metadata_seq IF EXISTS;
DROP VIEW test_metadata_view IF EXISTS;
DROP TABLE test_metadata_table CASCADE;
SET SCHEMA PUBLIC;
DROP SCHEMA test_schema CASCADE;