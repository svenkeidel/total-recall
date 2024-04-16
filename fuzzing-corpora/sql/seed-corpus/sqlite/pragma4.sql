
  CREATE TABLE t1(a, b, c);
  ATTACH 'test.db2' AS aux;
  CREATE TABLE aux.t2(d, e, f);

  0 a {
  0 d { 
  PRAGMA table_info(t1) 
 PRAGMA table_info(t2) 
  CREATE TABLE t1(a, b, c);
  ATTACH 'test.db2' AS aux;
  CREATE TABLE aux.t2(d, e, f);

    0 a {
    0 d { SELECT * FROM pragma_table_info('t1')  SELECT * FROM pragma_table_info('t2') 
  CREATE TABLE t1(a, b, c);
  CREATE INDEX i1 ON t1(b);
  ATTACH 'test.db2' AS aux;
  CREATE TABLE aux.t2(d, e, f);
  CREATE INDEX aux.i2 ON t2(e);
0 1 b0 1 e SELECT * FROM pragma_index_info('i1')  SELECT * FROM pragma_index_info('i2') 
  CREATE INDEX main.i1 ON t1(b, c);
  CREATE INDEX aux.i2 ON t2(e, f);
0 i1 0 c 00 i2 0 c 0
  CREATE UNIQUE INDEX main.i1 ON t1(a);
  CREATE UNIQUE INDEX aux.i2 ON t2(d);
  CREATE TABLE main.c1 (a, b, c REFERENCES t1(a));
  CREATE TABLE aux.c2 (d, e, r REFERENCES t2(d));

    0 0 t1 c a {NO ACTION
    0 0 t2 r d {NO ACTION SELECT * FROM pragma_foreign_key_list('c1')  SELECT * FROM pragma_foreign_key_list('c2') 
  CREATE TABLE main.c1 (a, b, c REFERENCES t1(a));
  CREATE TABLE aux.c2 (d, e, r REFERENCES t2(d));
  INSERT INTO main.c1 VALUES(1, 2, 3);
  INSERT INTO aux.c2 VALUES(4, 5, 6);

  c1 1 t1 0

  c2 1 t2 0
c1 1 t1 0
  CREATE TABLE t4(a DEFAULT 'abc' /* comment */, b DEFAULT -1 -- comment
     , c DEFAULT +4.0 /* another comment */
  );
  PRAGMA table_info = t4;
