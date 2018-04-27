head -n 121 ../10_data/model.csv | cut -f3- -d, > input2.csv

head -n 1331 ../10_data/model.csv | cut -f2- -d, > input3.csv

cp ../10_data/model.csv input4.csv

pause