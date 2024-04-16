temp n1main n2at1  n3at2  n4main n1temp n1at1 n1at2 n1main n1temp n1at1 n1at2 n1at1  n3at2  n4
  ATTACH 'file.db' AS aux;
  CREATE TABLE t1(x, y);
  CREATE TEMP TABLE t1(x, y);
  CREATE TABLE aux.t1(x, y);
 DROP TABLE t1  DROP TABLE t1  DROP TABLE t1 