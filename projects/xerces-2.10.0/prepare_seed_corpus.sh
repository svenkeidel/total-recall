CORPUS_DIR=$1

cp -r $PROJECT_PATH/seed-corpus/* $CORPUS_DIR
python3 $PROJECT_PATH/adapt_resource_refs.py $PROJECT_PATH