null_literal = {"null"}
int_literals = {"-1", "2"}
unsigned_int_literals = {"1", "2"}
float_literals = {"-1.0", "2.5"}
char_literals = {"'a'", "'b'"}
blob_literals = {"X'0123456789ABCDEF'"}
ascii_string_literals = {"'foo'", "'bar'"}
unicode_string_literals = {"'αβγ'", "'×÷'"}
bool_literals = {"true", "false"}

# Time literals for HSQLDB 2.0
time_literals = {
    "TIME '14:30:00'",
    "TIME '00:00:00'",
    "TIME '23:59:59'",
    "TIME '14:30:45.123'",
    "DATE '2024-01-15'",
    "TIMESTAMP '2024-01-15 14:30:00'",
    "TIMESTAMP '2024-01-15 14:30:45.123456'",
    "TIME '14:30:00+02:00'",
    "TIMESTAMP '2024-01-15 14:30:00+00:00'",
    "CURRENT_DATE",
    "CURRENT_TIME",
    "CURRENT_TIMESTAMP",
    "LOCALTIME",
    "LOCALTIMESTAMP"
}

year_to_month_literals = {
    "INTERVAL '1-06' YEAR TO MONTH",
    "INTERVAL '3-12' YEAR TO MONTH"
}

day_to_second_literals = {
    "INTERVAL '2 23:12:19' DAY TO SECOND",
    "INTERVAL '15 11:34:18' DAY TO SECOND"
}

array_literals = {
    "ARRAY[1, 2, 3]",
    "ARRAY[]",
    "ARRAY[NULL, 2, 3]"
}

types = {
    # Boolean
    "BOOLEAN": bool_literals.union(null_literal),

    # Exact numeric integers
    "TINYINT": int_literals.union(null_literal),
    "SMALLINT": int_literals.union(null_literal),
    "INTEGER": int_literals.union(null_literal),
    "BIGINT": int_literals.union(null_literal),

    # Exact numeric decimals
    "NUMERIC(5,5)": float_literals.union(null_literal),
    "NUMERIC(10,2)": float_literals.union(null_literal),
    "DECIMAL(5,5)": float_literals.union(null_literal),

    # Approximate numeric
    "REAL": float_literals.union(null_literal),
    "FLOAT": float_literals.union(null_literal),
    "FLOAT(10)": float_literals.union(null_literal),
    "DOUBLE PRECISION": float_literals.union(null_literal),

    # Character string
    "CHAR(10)": ascii_string_literals.union(null_literal),
    "VARCHAR(10)": ascii_string_literals.union(null_literal),
    "LONGVARCHAR": ascii_string_literals.union(null_literal),
    "CLOB(1K)": ascii_string_literals.union(null_literal),
    "CLOB": ascii_string_literals.union(null_literal),

    # Binary string
    "BINARY(10)": blob_literals.union(null_literal),
    "VARBINARY(10)": blob_literals.union(null_literal),
    "LONGVARBINARY": blob_literals.union(null_literal),
    "BLOB(1K)": blob_literals.union(null_literal),
    "BLOB": blob_literals.union(null_literal),

    # Date/Time - NEW in 2.0
    "DATE": time_literals.union(null_literal),
    "TIME": time_literals.union(null_literal),
    "TIMESTAMP": time_literals.union(null_literal),

    "INTERVAL YEAR TO MONTH": year_to_month_literals.union(null_literal),
    "INTERVAL DAY TO SECOND": day_to_second_literals.union(null_literal)
}

joins = [
    "LEFT JOIN",
    "LEFT OUTER JOIN",
    "RIGHT JOIN",
    "RIGHT OUTER JOIN",
    "INNER JOIN"
]

indices = [
    "ARRAY",
    "REDBLACK",
    "BTREE"
]