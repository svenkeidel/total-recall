null_literal = {"null"}
int_literals = {"-1", "2"}
unsigned_int_literals = {"1", "2"}
float_literals = {"-1.0", "2.5"}
char_literals = {"'a'", "'b'"}
blob_literals = {"'newlob()'"}
ascii_string_literals = {"'foo'", "'bar'"}
unicode_string_literals = {"'αβγ'", "'×÷'"}
bool_literals = {"true", "false"}

types = {
    "BOOLEAN": bool_literals.union(null_literal),
    "BYTE": int_literals.union(null_literal),
    "UNSIGNEDBYTE": unsigned_int_literals.union(null_literal),
    "SHORT": int_literals.union(null_literal),
    "UNSIGNEDSHORT": unsigned_int_literals.union(null_literal),
    "INTEGER": int_literals.union(null_literal),
    "UNSIGNEDINTEGER": unsigned_int_literals.union(null_literal),
    "LONG": int_literals.union(null_literal),
    "CHAR": char_literals.union(null_literal),
    "VARCHAR": ascii_string_literals.union(null_literal),
    "VARCHAR2": ascii_string_literals.union(null_literal),
    "FLOAT": int_literals.union(float_literals).union(null_literal),
    "NUMBER": int_literals.union(float_literals).union(null_literal),
    "CLOB": blob_literals.union(null_literal),
    "COMPRESSEDCLOB": blob_literals.union(null_literal),
    "BLOB": blob_literals.union(null_literal),
    "COMPRESSEDBLOB": blob_literals.union(null_literal),
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