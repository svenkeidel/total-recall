from sql_types import (types, joins, indices)
import os
import sys

corpus = sys.argv[1]


def gen_joins():
    try:
        os.makedirs(f"{corpus}/generated/")
    except FileExistsError:
        pass
    for t1, lits1 in types.items():
        for t2, lits2 in types.items():
            f = open(f"{corpus}/generated/convert_{t1}_{t2}.sql", "w")
            f.write(f"CREATE TABLE t1 (x {t1});\n")
            for lit in lits1:
                f.write(f"INSERT INTO t1 VALUES ({lit});\n")
            f.write(f"CREATE TABLE t2 (x {t2});\n")
            for lit in lits2:
                f.write(f"INSERT INTO t2 VALUES ({lit});\n")
            for join in joins:
                f.write(f"SELECT x FROM t1 {join} t2 ON (t1.x = t2.x);\n")
            f.write(f"DROP TABLE t1;\n")
            f.write(f"DROP TABLE t2;\n")
            f.close()


def gen_idx():
    try:
        os.makedirs(f"{corpus}/generated/")
    except FileExistsError:
        pass
    for t1, lits1 in types.items():
        f = open(f"{corpus}/generated/idx_{t1}.sql", "w")
        f.write(f"CREATE TABLE t1 (x {t1});\n")
        for lit in lits1:
            f.write(f"INSERT INTO t1 VALUES ({lit});\n")
        for index in indices:
            f.write(f"CREATE {index} INDEX {index} ON t1 (x);\n")
            f.write(f"SELECT * from t1;\n")
        f.write(f"DROP TABLE t1;\n")
        f.close()


gen_joins()
gen_idx()
