-- Test: All HSQLDB 2.0 data types
-- Run: java -jar sqltool.jar --rcFile sqltool.rc localhsql test_datatypes.sql

CREATE TABLE test_datatypes (
    -- Boolean
                                col_boolean BOOLEAN,

    -- Exact numeric integers
                                col_tinyint TINYINT,
                                col_smallint SMALLINT,
                                col_integer INTEGER,
                                col_bigint BIGINT,

    -- Exact numeric decimals
                                col_numeric NUMERIC(10, 2),
                                col_decimal DECIMAL(10, 2),

    -- Approximate numeric
                                col_real REAL,
                                col_float FLOAT,
                                col_double DOUBLE PRECISION,

    -- Character strings
                                col_char CHAR(10),
                                col_varchar VARCHAR(50),
                                col_clob CLOB(1K),
                                col_longvarchar LONGVARCHAR,

    -- Binary strings
                                col_binary BINARY(4),
                                col_varbinary VARBINARY(10),
                                col_blob BLOB(1K),

    -- Date/Time (HSQLDB 2.0 features)
                                col_date DATE,
                                col_time TIME,
                                col_time_3 TIME(3),
                                col_timestamp TIMESTAMP,
                                col_timestamp_6 TIMESTAMP(6),

    -- Time zone types (NEW in HSQLDB 2.0)
                                col_time_tz TIME WITH TIME ZONE,
                                col_timestamp_tz TIMESTAMP WITH TIME ZONE,

    -- Intervals (NEW in HSQLDB 2.0)
                                col_interval_ym INTERVAL YEAR(2) TO MONTH,
                                col_interval_ds INTERVAL DAY(3) TO SECOND(3),

    -- Array (NEW in HSQLDB 2.0)
                                col_array INTEGER ARRAY[5],

);

-- Insert test values
INSERT INTO test_datatypes VALUES (
  TRUE,                           -- BOOLEAN

  127,                            -- TINYINT
  32767,                          -- SMALLINT
  2147483647,                     -- INTEGER
  9223372036854775807,            -- BIGINT

  12345678.90,                    -- NUMERIC
  98765432.10,                    -- DECIMAL

  3.14159,                        -- REAL
  2.718281828,                    -- FLOAT
  1.4142135623730951,             -- DOUBLE

  'FIXED',                        -- CHAR (will be padded)
  'Variable length',              -- VARCHAR
  'Large character object text',  -- CLOB
  'Long varchar text',            -- LONGVARCHAR

  X'DEADBEEF',                    -- BINARY
  X'CAFEBABE00',                  -- VARBINARY
  X'0123456789ABCDEF',            -- BLOB

  DATE '2024-06-15',              -- DATE
  TIME '14:30:00',                -- TIME
  TIME '14:30:45.123',            -- TIME(3)
  TIMESTAMP '2024-06-15 14:30:00', -- TIMESTAMP
  TIMESTAMP '2024-06-15 14:30:45.123456', -- TIMESTAMP(6)

  TIME '14:30:00+02:00',          -- TIME WITH TIME ZONE (NEW 2.0)
  TIMESTAMP '2024-06-15 14:30:00+00:00', -- TIMESTAMP WITH TIME ZONE (NEW 2.0)

  INTERVAL '-23-10' YEAR(3) TO MONTH,                                  -- INTERVAL YM (NEW 2.0)
  INTERVAL '145 23:12:19.345' DAY(3) TO SECOND(3),                         -- INTERVAL DS (NEW 2.0)

  ARRAY[10, 20, 30, 40, 50]   -- ARRAY (NEW 2.0)
);

-- Insert NULL values test
INSERT INTO test_datatypes VALUES (
  NULL, NULL, NULL, NULL, NULL,
  NULL, NULL, NULL, NULL, NULL,
  NULL, NULL, NULL, NULL,
  NULL, NULL, X'00',
  NULL, NULL, NULL,
  NULL, NULL, NULL, NULL, NULL,
  NULL, NULL
);

CHECKPOINT DEFRAG;

-- Create compressed backup
BACKUP DATABASE TO 'test_datatypes.tar.gz' BLOCKING;

-- Verify data
SELECT
    col_boolean,
    col_tinyint,
    col_smallint,
    col_integer,
    col_bigint,
    col_numeric,
    col_decimal,
    col_real,
    col_float,
    col_double,
    col_char,
    LENGTH(col_char) AS char_length,
    col_varchar,
    col_clob,
    col_binary,
    col_varbinary,
    CASE WHEN col_blob IS NOT NULL THEN col_blob ELSE X'00' END as col_blob,
    col_date,
    col_time,
    col_time_3,
    col_timestamp,
    col_timestamp_6,
    col_time_tz,
    col_timestamp_tz,
    col_interval_ym,
    col_interval_ds,
    col_array
FROM test_datatypes WHERE col_boolean IS NOT NULL;

SELECT * FROM test_datatypes WHERE col_boolean IS NULL;

-- Test array element access (NEW 2.0)
SELECT col_array[1] AS first_element, col_array[5] AS last_element FROM test_datatypes WHERE col_array IS NOT NULL;

-- Cleanup
DROP TABLE test_datatypes CASCADE;