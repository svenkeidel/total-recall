mkdir -p /app/data
python ./prepare_data.py /in /app/data

dir=/data/$(date -d "today" +"%Y%m%d%H%M")
mkdir $dir
cp -r /app/data $dir

jupyter execute ./Plot_Precision_Recall.ipynb