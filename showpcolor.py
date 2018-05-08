
"""
Demonstrates similarities between pcolor, pcolormesh, imshow and pcolorfast
for drawing quadrilateral grids.

"""
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.animation as animation
import pandas as pd

def show_result(sn):
    aa = np.arange(-1.0, 1.0001, 0.2)
    bb = np.arange(-1.0, 1.0001, 0.2)
    xx, yy = np.meshgrid(aa, bb)
    zz = (xx+yy)/2

    df = pd.read_csv("input2.csv", sep=',', header=None, names=['x', 'y', 'value'])
    for key, row in df.iterrows():
        x = int(row['x'])
        y = int(row['y'])
        v = float(row['value'])
        zz[x,y] = v

    df_bandit = pd.read_csv("log2_ts.csv", sep=',', header=None, names=['l', 'x', 'y', 'exp', 'value'])
    print(df_bandit[['x', 'y', 'value']][:sn])
    print(np.average(df_bandit['value'][:sn]))

    x_org = np.arange(0, sn, 1)
    y_sin = np.sin(x_org/10)
    y_cos = np.cos(x_org/10)
    y_sin = np.array(df_bandit['value'][:sn])
    '''
    for s in np.arange(sn):
        ave = np.mean(df_bandit['value'][:s])
        y_sin[s] = ave
    '''

    plt.subplot(2, 2, 1)
    plt.pcolor(xx, yy, zz, cmap='RdBu', vmin=-2, vmax=2)
    plt.title('original')
    # set the limits of the plot to the limits of the data
    plt.axis([xx.min(), xx.max(), yy.min(), yy.max()])
    plt.colorbar()


    plt.subplot(2, 2, 2)
    plt.plot(x_org, y_sin)
    plt.hlines([-1, 1], 0, 300, linestyles="dashed")

    plt.subplot(2, 2, 3)
    plt.pcolor(xx, yy, zz, cmap='RdBu', vmin=-2, vmax=2)
    plt.title('bandit')
    # set the limits of the plot to the limits of the data
    plt.axis([xx.min(), xx.max(), yy.min(), yy.max()])
    plt.colorbar()

    plt.subplot(2, 2, 4)
    plt.plot(x_org, y_cos)
    plt.hlines([-1, 1], 0, 300, linestyles="dashed")

    plt.subplots_adjust(wspace=0.5, hspace=0.5)

    plt.show()

show_result(200)
