
  CREATE VIRTUAL TABLE x1 USING tcl(vtab_command);

  CREATE TABLE t1x(i INTEGER PRIMARY KEY, a, b);
  INSERT INTO t1x VALUES(1, 'one', 1);
  INSERT INTO t1x VALUES(2, 'two', 2);
  INSERT INTO t1x VALUES(3, 'three', 3);
  INSERT INTO t1x VALUES(4, 'four', 4);
one 1 two 2 three 3 four 41 2 3 42
    SELECT rowid FROM t1 WHERE a IN ('one', 'four') ORDER BY +rowid
  
  CREATE VIRTUAL TABLE VirtualTableA USING tcl(vtab_command);
  CREATE VIRTUAL TABLE VirtualTableB USING tcl(vtab_command);
1 2 3 4
  SELECT * FROM 
  VirtualTableA a CROSS JOIN VirtualTableB b ON b.PrimaryKey=a.PrimaryKey
  WHERE a.ColumnA IN ('ValueA', 'ValueB') AND a.FlagA=0

  SELECT * FROM 
  VirtualTableA a CROSS JOIN VirtualTableB b ON b.PrimaryKey=a.PrimaryKey
  WHERE a.FlagA=0 AND a.ColumnA IN ('ValueA', 'ValueB') 

  CREATE VIRTUAL TABLE x1 USING tcl(vtab_command);
