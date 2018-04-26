import sys
import numpy as np
import pandas as pd
import math

def L2(a, b):
    L2 = (a-b)*(a-b)
    return L2

'''
def L2_b(a, b):
    L2 = 0.0
    if a!=b:
        L2 = 400.0
    return L2
'''

def dist2(row1, x1, x2, x3, x4):
    sum2 = 0.0
    sum2 += L2(row1['x1'], x1)
    sum2 += L2(row1['x2'], x2)
    sum2 += L2(row1['x3'], x3)
    sum2 += L2(row1['x4'], x4)
    dist2 = math.sqrt(sum2)
    return dist2

def calc_y(df_input, x1, x2, x3, x4):
    sum_y = 0.0
    sum_w = 0.0
    for key, row in df_input.iterrows():
        d = dist2(row, x1, x2, x3, x4)
        w = math.exp(-d*d/(2*1*1))
        sum_y += row['y']*w
        sum_w += w
    y = sum_y/sum_w
    return y

def run(filename_input, filename_output):
    df_input = pd.read_csv(filename_input, delimiter=',')
    print(df_input)

    fw = open(filename_output, 'w')
    for x1 in np.arange(11):
        print(" " + str(x1))
        for x2 in np.arange(11):
            print(" == " + str(x2))
            for x3 in np.arange(11):
                print(" ==== " + str(x3))
                for x4 in np.arange(11):
                    y = calc_y(df_input, x1, x2, x3, x4)
                    fw.write(str(x1) + ','+ str(x2) + ','+ str(x3) + ','+ str(x4) + ','+ str(y) + '\n')
    fw.close()

def main():
    filename_input = sys.argv[1]
    filename_output = sys.argv[2]

    run(filename_input, filename_output)

main()
