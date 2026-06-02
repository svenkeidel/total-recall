-- Test: Backup, checkpoint, export/import, scripting
-- Run: java -jar sqltool.jar --rcFile sqltool.rc localhsql test_backup_export.sql

CREATE TABLE test_backup_data (
                                  id INTEGER PRIMARY KEY,
                                  data VARCHAR(100),
                                  created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO test_backup_data VALUES (1, 'Important data 1', CURRENT_TIMESTAMP);
INSERT INTO test_backup_data VALUES (2, 'Important data 2', CURRENT_TIMESTAMP);
INSERT INTO test_backup_data VALUES (3, 'Important data 3', CURRENT_TIMESTAMP);

-- Defragment and checkpoint
CHECKPOINT DEFRAG;

-- Also create script to memory/variable
SCRIPT;

-- Create compressed backup
BACKUP DATABASE TO 'test_backup.tar.gz' BLOCKING;

-- Non-blocking backup (HSQLDB 2.0)
-- BACKUP DATABASE TO 'test_backup_nb.tar' NOT BLOCKING AS FILES;

-- Export table to CSV-like format
SELECT * FROM test_backup_data;

-- Use built-in CSV write (if available via function)
-- Or use standard SQL to generate CSV
SELECT id || ',' || '"' || REPLACE(data, '"', '""') || '"' || ',' || created
FROM test_backup_data;

-- ============================================
-- Export using Java stored procedure (if available)
-- ============================================

-- Alternative: use text table for import/export
CREATE TEXT TABLE test_text_export (
    id INTEGER,
    data VARCHAR(100)
);

SET TABLE test_text_export SOURCE 'test_export.csv;fs=,;ignore_first=false';

INSERT INTO test_text_export SELECT id, data FROM test_backup_data;

-- Sync to file
SET TABLE test_text_export SOURCE OFF;
SET TABLE test_text_export SOURCE 'test_export.csv;fs=,;ignore_first=false';

SELECT * FROM test_text_export;

-- ============================================
-- Cleanup and restore tests
-- ============================================

DROP TABLE test_text_export IF EXISTS;

-- Verify data integrity after operations
SELECT COUNT(*) AS row_count, MIN(id) AS min_id, MAX(id) AS max_id FROM test_backup_data;

-- Shutdown commands (use with care)
-- SHUTDOWN;           -- Normal checkpoint and close
-- SHUTDOWN COMPACT;   -- Defragment and close
-- SHUTDOWN SCRIPT;    -- Write script, close

DROP TABLE test_backup_data CASCADE;