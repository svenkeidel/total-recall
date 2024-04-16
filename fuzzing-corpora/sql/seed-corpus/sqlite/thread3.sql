
  CREATE TABLE t1(a, b);
  PRAGMA journal_mode = DELETE;

  SELECT count(*) FROM t1;
