import numpy as np
import matplotlib.pyplot as plt
import matplotlib.animation as animation
import pandas as pd
import math

fig = plt.figure(figsize=(6.4,4.8))
b_colorbar1 = True
b_colorbar2 = True

def show_result(sn0):
    sn = sn0*2
    print('sn=' + str(sn))

    df_bandit = pd.read_csv("log4_ts.csv", sep=',', header=None, names=['l', 'x1', 'x2', 'x3', 'x4', 'exp', 'value'])
    df_bandit_sn = df_bandit[['x1', 'x2', 'x3', 'x4', 'value']][:sn]
    #print(df_bandit_sn)
    #print(np.average(df_bandit['value'][:sn]))

    df_original = pd.read_csv("original4.csv", sep=',', header=None, names=['x1', 'x2', 'x3', 'x4', 'value'])
    df_original_sn = df_original[['x1', 'x2', 'x3', 'x4', 'value']][:sn]

    x_org = np.arange(0, sn, 1)

    y_original = np.cos(x_org/10)
    y_original = np.array(df_original_sn['value'])
    y_original_mean = np.zeros(sn)

    y_bandit = np.sin(x_org/10)
    y_bandit = np.array(df_bandit_sn['value'])
    y_bandit_mean = np.zeros(sn)

    for s in np.arange(sn):
        b = s - 100
        if b<0:
            b=0
        bandit_ave = np.mean(df_bandit['value'][b:s])
        y_bandit_mean[s] = bandit_ave
        original_ave = np.mean(df_original['value'][b:s])
        y_original_mean[s] = original_ave

    plt.axes([0.1, 0.1, 0.8, 0.3])
    plt.cla()
    plt.title('bandit(' + str(sn) + ')')
    plt.plot(x_org, y_bandit, color='green')
    plt.plot(x_org, y_bandit_mean, color='red', linewidth = 3.0)
    plt.hlines([-3, -2, -1, 1, 2, 3], 0, 600, linestyles=":")
    plt.hlines([0], 0, 600, linestyles="-")

    plt.axes([0.1, 0.6, 0.8, 0.3])
    plt.cla()
    plt.title('original(' + str(sn) + ')')
    plt.plot(x_org, y_original, color='green')
    plt.plot(x_org, y_original_mean, color='red', linewidth = 3.0)
    plt.hlines([-3, -2, -1, 1, 2, 3], 0, 600, linestyles=":")
    plt.hlines([0], 0, 600, linestyles="-")

    plt.subplots_adjust(wspace=0.5, hspace=0.5)

def plot(data):
    print(data)
    plt.cla()
    rand = np.random.randn(100)
    #im = plt.plot(rand)
    plt.plot(rand)

def show_animation():
    ani = animation.FuncAnimation(fig, show_result, interval=100, frames=300)
    #plt.show()
    ani.save("result.htm")

show_animation()
#show_plot()
