CORPUS_DIR=$1

cp -r $PROJECT_PATH/seed-corpus/* $CORPUS_DIR
python3 $PROJECT_PATH/generate-corpus/gen_sql.py $CORPUS_DIR
