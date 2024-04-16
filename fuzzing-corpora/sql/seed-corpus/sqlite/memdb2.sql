
    CREATE TABLE t1(x, y);
    INSERT INTO t1 VALUES(1, 2);
  
    BEGIN;
      SELECT * FROM t1;
  
    BEGIN;
      INSERT INTO t1 VALUES(3, 4);
  
      SELECT * FROM t1;
    END;
  
    COMMIT
  
    SELECT * FROM t1
  