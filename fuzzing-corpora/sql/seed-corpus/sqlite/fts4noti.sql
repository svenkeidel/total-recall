
  CREATE TABLE cc(a, b, c);
cc
    INSERT INTO t1(docid,a,b,c) VALUES(1, 'one two', 'three four', 'five six');
    INSERT INTO t1(docid,a,b,c) VALUES(2, 'three four', 'five six', 'one two');
  1 221 INSERT INTO t1(t1) VALUES('optimize') 1 221 INSERT INTO t1(t1) VALUES('rebuild') 1 221 
      SELECT a,b,c FROM t1 WHERE docid=1
     
      SELECT a,b,c FROM t1 WHERE docid=2
     DROP TABLE t1 
  CREATE VIRTUAL TABLE t2 USING fts4(x, y, notindexed=x);
2 3 4 5 61 22 DROP TABLE t2 
  CREATE VIRTUAL TABLE t2 USING fts4(poi, addr, notindexed=poi);
  INSERT INTO t2 VALUES(114, 'x x x');
  INSERT INTO t2 VALUES(X'1234', 'y y y');
  INSERT INTO t2 VALUES(NULL, 'z z z');
  INSERT INTO t2 VALUES(113.2, 'w w w');
  INSERT INTO t2 VALUES('poi', 'v v v');
integerblobnullrealtext DROP TABLE t2 
  CREATE VIRTUAL TABLE t2 USING fts4(
      notindexed="three", one, two, three, notindexed="one",
  );
  INSERT INTO t2 VALUES('a', 'b', 'c');
  INSERT INTO t2 VALUES('c', 'a', 'b');
  INSERT INTO t2 VALUES('b', 'c', 'a');
213 DROP TABLE t2 
  CREATE VIRTUAL TABLE t1 USING fts4(
    poiCategory, poiCategoryId, notindexed=poiCategoryId
  );
  INSERT INTO t1(poiCategory, poiCategoryId) values ('Restaurant', 6021);

  SELECT * FROM t1 WHERE t1 MATCH 'restaurant';

  SELECT * FROM t1 WHERE t1 MATCH 're*';

  SELECT * FROM t1 WHERE t1 MATCH '6021';

  SELECT * FROM t1 WHERE t1 MATCH '60*';

  DROP TABLE t1;
  CREATE VIRTUAL TABLE t1 USING fts4(
    poiCategory, poiCategoryId, notindexed=poiCategory
  );
  INSERT INTO t1(poiCategory, poiCategoryId) values ('Restaurant', 6021);

  SELECT * FROM t1 WHERE t1 MATCH 'restaurant';

  SELECT * FROM t1 WHERE t1 MATCH 're*';

  SELECT * FROM t1 WHERE t1 MATCH '6021';

  SELECT * FROM t1 WHERE t1 MATCH '60*';

  DROP TABLE t1;
  CREATE VIRTUAL TABLE t1 USING fts4(abc, ab, a, notindexed=abc);
  CREATE VIRTUAL TABLE t2 USING fts4(a, ab, abc, notindexed=abc);

  INSERT INTO t1 VALUES('no', 'yes', 'yep');
  INSERT INTO t2 VALUES('yep', 'yes', 'no');

  SELECT count(*) FROM t1 WHERE t1 MATCH 'no';
  SELECT count(*) FROM t1 WHERE t1 MATCH 'yes';
  SELECT count(*) FROM t1 WHERE t1 MATCH 'yep';

  SELECT count(*) FROM t2 WHERE t2 MATCH 'no';
  SELECT count(*) FROM t2 WHERE t2 MATCH 'yes';
  SELECT count(*) FROM t2 WHERE t2 MATCH 'yep';
