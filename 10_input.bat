rem head -n 121 ../10_data/model.csv | cut -f3- -d, > input2.csv
head -n 7381 ../10_data/model.csv | tail -n 121 | cut -f3- -d, > input2.csv

rem head -n 1331 ../10_data/model.csv | cut -f2- -d, > input3.csv
head -n 7986 ../10_data/model.csv | tail -n 1331 | cut -f2- -d, > input3.csv

cp ../10_data/model.csv input4.csv

pause