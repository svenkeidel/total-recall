
  CREATE TABLE x1(a INTEGER PRIMARY KEY, b DOTS);
  INSERT INTO x1 VALUES(-1, $dots);
  INSERT INTO x1 VALUES(-10, $dots);
  INSERT INTO x1 VALUES(-100, $dots);
  INSERT INTO x1 VALUES(-1000, $dots);
  INSERT INTO x1 VALUES(-10000, $dots);
main unlocked temp closedmain shared temp closedmain unlocked temp closedmain unlocked temp closed
  SELECT * FROM x1 WHERE a IN (1, -10);
