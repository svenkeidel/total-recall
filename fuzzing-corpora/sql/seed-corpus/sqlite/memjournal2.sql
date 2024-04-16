
  PRAGMA journal_mode = memory;
  CREATE TABLE t1(a INTEGER PRIMARY KEY, b UNIQUE);

  BEGIN;
    WITH s(i) AS (
      SELECT 1 UNION ALL SELECT i+1 FROM s WHERE i<$nRow
    )
    INSERT INTO t1 SELECT NULL, randomblob(700) FROM s;

    SAVEPOINT one; 
      UPDATE t1 SET b=randomblob(700) WHERE a<=$jj;
  
      SAVEPOINT two;
        UPDATE t1 SET b=randomblob(700) WHERE a==1;
      ROLLBACK TO two;
      RELEASE two;
  
      SAVEPOINT two;
        UPDATE t1 SET b=randomblob(700) WHERE a==1;
      ROLLBACK TO two;
      RELEASE two;
  
    PRAGMA integrity_check;
    ROLLBACK TO one;
    RELEASE one;
  