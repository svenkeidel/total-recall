
    PRAGMA encoding = 'utf16';
    CREATE TABLE t2(x);
    INSERT INTO t2 VALUES('text2');
  
    PRAGMA encoding = 'utf16';
    CREATE TABLE t3(x);
    INSERT INTO t3 VALUES('text3');
  
    PRAGMA encoding = 'utf8';
    CREATE TABLE t4(x);
    INSERT INTO t4 VALUES('text4');
  
    PRAGMA encoding = 'utf16';
    ATTACH 'test.db2' AS aux;
    SELECT * FROM t2;
  
    ATTACH 'test.db4' AS aux;
    SELECT * FROM t4;
  
    ATTACH 'test.db3' AS aux;
    SELECT * FROM t3;
    SELECT * FROM t2;
  