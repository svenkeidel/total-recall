
  PRAGMA cache_size = 10;
  BEGIN;
    INSERT INTO t1 VALUES(1, randomblob(1500));
    INSERT INTO t1 VALUES(2, randomblob(1500));
    INSERT INTO t1 VALUES(3, randomblob(1500));
    SELECT * FROM counter;
3 01 2 3
    BEGIN;
      INSERT INTO z VALUES(NULL, a_string(800));
      INSERT INTO z VALUES(NULL, a_string(800));
      SAVEPOINT one;
        UPDATE z SET y = NULL WHERE x>256;
        PRAGMA incremental_vacuum;
        SELECT count(*) FROM z WHERE x < 100;
      ROLLBACK TO one;
    COMMIT;
  
    BEGIN;
      SAVEPOINT one;
        UPDATE z SET y = y||x;
      ROLLBACK TO one;
    COMMIT;
    SELECT count(*) FROM z;
  
    SAVEPOINT one;
      UPDATE z SET y = y||x;
    ROLLBACK TO one;
  
    SELECT count(*) FROM z;
    RELEASE one;
    PRAGMA integrity_check;
  
    SAVEPOINT one;
    RELEASE one;
  
      SELECT * FROM a
    
      SELECT * FROM b
    double-you why zedwon too free
      SELECT * FROM a
    
      SELECT * FROM b
    
  PRAGMA journal_mode = DELETE;
  PRAGMA page_size = 1024;
  CREATE TABLE t1(a, b);
  CREATE TABLE t2(a, b);
  INSERT INTO t1 VALUES('I', 'II');
  INSERT INTO t2 VALUES('III', 'IV');
  BEGIN;
    INSERT INTO t1 VALUES(1, 2);
    INSERT INTO t2 VALUES(3, 4);
  COMMIT;

  SELECT * FROM t1;
  SELECT * FROM t2;

  SELECT * FROM t1;
  SELECT * FROM t2;

  SELECT * FROM t1;
  SELECT * FROM t2;

  SELECT * FROM t1;
  SELECT * FROM t2;
1 t1.1
  ATTACH 'test.db2' AS two;
  SELECT * FROM t2;
1 t1.1
  ATTACH 'test.db2' AS two;
  SELECT * FROM t2;

  ATTACH 'test.db3' AS three;
  SELECT * FROM t3;

  PRAGMA journal_mode = DELETE;
  CREATE TABLE t1(x PRIMARY KEY, y);
  CREATE INDEX i1 ON t1(y);
  INSERT INTO t1 VALUES('I',   'one');
  INSERT INTO t1 VALUES('II',  'four');
  INSERT INTO t1 VALUES('III', 'nine');
  BEGIN;
    INSERT INTO t1 VALUES('IV', 'sixteen');
    INSERT INTO t1 VALUES('V' , 'twentyfive');
  COMMIT;
1015
  BEGIN;
    INSERT INTO t11 VALUES(1, 2);
    PRAGMA max_page_count = 13;

    INSERT INTO t11 VALUES(3, 4);
    PRAGMA max_page_count = 10;
111 2 3 411
    BEGIN;
      INSERT INTO x1 VALUES('William');
      INSERT INTO x1 VALUES('Anne');
    ROLLBACK;
  
  PRAGMA journal_mode = DELETE;
  PRAGMA cache_size = 10;
  BEGIN;
    CREATE TABLE zz(top PRIMARY KEY);
    INSERT INTO zz VALUES(a_string(222));
    INSERT INTO zz SELECT a_string((SELECT 222+max(rowid) FROM zz)) FROM zz;
    INSERT INTO zz SELECT a_string((SELECT 222+max(rowid) FROM zz)) FROM zz;
    INSERT INTO zz SELECT a_string((SELECT 222+max(rowid) FROM zz)) FROM zz;
    INSERT INTO zz SELECT a_string((SELECT 222+max(rowid) FROM zz)) FROM zz;
    INSERT INTO zz SELECT a_string((SELECT 222+max(rowid) FROM zz)) FROM zz;
  COMMIT;
  BEGIN;
    UPDATE zz SET top = a_string(345);
32
  PRAGMA page_size = 1024;
  PRAGMA journal_mode = PERSIST;
  PRAGMA cache_size = 10;
  BEGIN;
    CREATE TABLE t1(a INTEGER PRIMARY KEY, b BLOB);
    INSERT INTO t1 VALUES(NULL, a_string(400));
    INSERT INTO t1 SELECT NULL, a_string(400) FROM t1;          /*   2 */
    INSERT INTO t1 SELECT NULL, a_string(400) FROM t1;          /*   4 */
    INSERT INTO t1 SELECT NULL, a_string(400) FROM t1;          /*   8 */
    INSERT INTO t1 SELECT NULL, a_string(400) FROM t1;          /*  16 */
    INSERT INTO t1 SELECT NULL, a_string(400) FROM t1;          /*  32 */
    INSERT INTO t1 SELECT NULL, a_string(400) FROM t1;          /*  64 */
    INSERT INTO t1 SELECT NULL, a_string(400) FROM t1;          /* 128 */
  COMMIT;
  UPDATE t1 SET b = a_string(400);
 
    UPDATE t1 SET b = a_string(399) WHERE a <= $nUp
  ok
  CREATE INDEX i1 ON t1(b);
  UPDATE t1 SET b = a_string(400);
 
    UPDATE t1 SET b = a_string(399) WHERE a <= $nUp
  ok
  PRAGMA journal_mode = OFF;
  CREATE TABLE t1(a, b);
  BEGIN;
    INSERT INTO t1 VALUES(1, 2);
  COMMIT;
  SELECT * FROM t1;

  SELECT * FROM t1;

  COMMIT;

  SELECT * FROM t1;

  CREATE TABLE tx(y, z);
  INSERT INTO tx VALUES('Ayutthaya', 'Beijing');
  INSERT INTO tx VALUES('London', 'Tokyo');

    SELECT * FROM tx;
  
  UPDATE tbl SET b = a_string(550);

    CREATE TABLE t1(a, b);
    INSERT INTO t1 VALUES(1, 2);
  
    BEGIN;
    INSERT INTO t1 VALUES(2, a_string($strsize));
    DELETE FROM t1 WHERE oid=2;
    COMMIT;
    PRAGMA integrity_check;
  
    VACUUM;
    SELECT * FROM t1;
  
  PRAGMA auto_vacuum = 2;
  CREATE TABLE t3(x);
  CREATE TABLE t4(x);

  DROP TABLE t2;
  DROP TABLE t3;
  DROP TABLE t4;

  PRAGMA page_size=4096;
  PRAGMA auto_vacuum=FULL;
  CREATE TABLE t1(a INTEGER PRIMARY KEY, b ANY);
  WITH RECURSIVE c(x) AS (VALUES(1) UNION ALL SELECT x+1 FROM c WHERE x<50)
  INSERT INTO t1(a,b) SELECT x, zeroblob(1000) FROM c;
  CREATE TABLE t2 AS SELECT * FROM t1;
  PRAGMA page_count;

  BEGIN;
  DROP TABLE t2;
  PRAGMA incremental_vacuum=50;
  PRAGMA page_count;
  PRAGMA max_page_count=2;

  ROLLBACK;
  PRAGMA page_count;
  PRAGMA max_page_count;
