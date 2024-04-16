
    SELECT b,a,c FROM t1 ORDER BY b,a,c;
  
    SELECT b,a,c FROM t1 ORDER BY b,c DESC,a;
  
    SELECT b,a,c FROM t1 ORDER BY b DESC,c,a;
  
    SELECT b,a,c FROM t1 ORDER BY b DESC,a,c;
  
    SELECT a FROM t1 ORDER BY b, a LIMIT 10 OFFSET 20;
  
    SELECT a FROM t1 ORDER BY +b, a LIMIT 10 OFFSET 20;
  
    SELECT a FROM t1 ORDER BY b DESC, a LIMIT 10 OFFSET 20;
  
    SELECT a FROM t1 ORDER BY +b DESC, a LIMIT 10 OFFSET 20;
  
    SELECT a FROM t1 ORDER BY b, a DESC LIMIT 10 OFFSET 45;
  
    SELECT a FROM t1 ORDER BY +b, a DESC LIMIT 10 OFFSET 45;
  
    SELECT a FROM t1 ORDER BY b DESC, a LIMIT 10 OFFSET 45;
  
    SELECT a FROM t1 ORDER BY +b DESC, a LIMIT 10 OFFSET 45;
  
    SELECT a FROM t2 ORDER BY b,c,d,e,f;
  
    SELECT a FROM t2 ORDER BY b,c,d,e,+f;
  
    SELECT a FROM t2 ORDER BY b,c,d,+e,+f;
  
    SELECT a FROM t2 ORDER BY b,c,+d,+e,+f;
  
    SELECT a FROM t2 ORDER BY b,+c,+d,+e,+f;
  
    SELECT a FROM t2 ORDER BY b,c,d,e,f DESC;
  
    SELECT a FROM t2 ORDER BY b,c,d,e DESC,f;
  
    SELECT a FROM t2 ORDER BY b,c,d DESC,e,f;
  
    SELECT a FROM t2 ORDER BY b,c DESC,d,e,f;
  
    SELECT a FROM t2 ORDER BY b DESC,c,d,e,f;
  
    SELECT a FROM t2 ORDER BY b DESC,c DESC,d,e,f LIMIT 31;
  
    SELECT a FROM t2 ORDER BY b,c,d,e,f DESC LIMIT 8 OFFSET 7;
  