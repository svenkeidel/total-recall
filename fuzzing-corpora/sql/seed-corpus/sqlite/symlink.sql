
  CREATE TABLE t1(x, y);

    SELECT * FROM t1;
  
    DELETE FROM t1;
    PRAGMA journal_mode = delete;
  
  CREATE TABLE xyz(x, y, z);
  INSERT INTO xyz VALUES(1, 2, 3);

  SELECT * FROM xyz;

  SELECT * FROM xyz;
