""" 

File that plots the results obtained in experiment_config.py

It is a messy script, because it needs to be adapted too often to get the right 
plots. Maybe in the end there'll be a version that's more neat. 

"""

import pandas as pd
import matplotlib.cm as cm
import matplotlib.pyplot as plt
import re 
import json 
import numpy as np
import seaborn as sns

# sets a nice plotting theme for all the plot in the script
sns.set_theme() 

# have fontdict for titles as global variable so they are the same for every plot 
fontdict = {'family': 'serif',
        'color':  'k',
        'weight': 'normal',
        'size': 16}


def plot_datasets(src='results/dataset_params.json'):
    """ 
    Function that plots the distribution of the datasets.

    It is put in a function so it does not run automatically
    """

    df = pd.read_json(src)
    df = df.T # to switch the colums and rows, initially datasets were columns

    fig, ax = plt.subplots()
    ax.scatter(x=df['n_samples'], y=df['n_features'])

    plt.title('Distribution of datasets in synthetic_data dir', fontdict=fontdict)
    plt.xlabel('Number of samples')
    plt.ylabel('Number of features')
    fig.savefig("results/dataset_distribution.pdf", bbox_inches='tight') ## save high quality plot 


def plot_run_times(X_axis, algorithm, compare=False, with_color=False):

    """
    Function that does unsupervised feature selection with laplacian score.

    PARAMS:
    X_axis: what you want the x-axis to represent. 
        The options are features, objects and objects times features.
        The latter you write as objects_x_features 

    algorithm: the algorithm you want analyzed. It automatically grabs the
        right dataset. 

    compare: to potentially compare another file with results
        which were received with running it again in peregrine. 

    ------------
    Returns:

    Nothing, but saves a pdf of the plot in the plots directory.
    """

    src = f'results/{algorithm}_peregrine.json'
    # src = f'results/{algorithm}_standardized.json'

    # create a dataframe from a json file 
    with open(src) as json_file:
        data = json.load(json_file)

            
    runtimes = []
    objects = []
    features = []
    objects_x_features = []
    None_values = 0
    None_datasets = []

    # print(algorithm, len(data[algorithm].items()))
    
    for key, value in data[algorithm].items():

        if value['run_time'] == None:
            None_values += 1
            None_datasets.append(key)

        else:
            split_values = re.split(r'(\d+)', key)
            
            objects.append(int(split_values[1]))
            features.append(int(split_values[3]))
            objects_x_features.append(int(split_values[1]) * int(split_values[3]))
            
            runtimes.append(float(value['run_time']))

    if compare:

        runtimes_compare = []
        objects_compare = []
        features_compare = []
        objects_x_features_compare = []
        None_values_compare = 0
        None_datasets_compare = []

        src2 = f'results/{algorithm}_standardized.json'

        with open(src2) as json_file:
            data_compare = json.load(json_file)
        

        for key, value in data_compare[algorithm].items():
        
            if value['run_time'] == None:
                None_values_compare += 1
                None_datasets_compare.append(key)

            else:
                split_values = re.split(r'(\d+)', key)
                
                objects_compare.append(int(split_values[1]))
                features_compare.append(int(split_values[3]))
                objects_x_features_compare.append(int(split_values[1]) * int(split_values[3]))
                
                runtimes_compare.append(float(value['run_time']))

        X_compare = eval(X_axis+'_compare')
    # get the desired values for the x axis:
    # either features, objects, object_x_features
    X = eval(X_axis)

    colors = [] 
    fig, ax = plt.subplots()

    if with_color:

        if X_axis == 'objects':
            c = cm.jet((features-np.min(features)) / (np.max(features)-np.min(features)))
        
        elif X_axis == 'features':
            c = cm.jet((objects-np.min(objects)) / (np.max(objects)-np.min(objects)))

        else: 
            c = ['black'] * len(objects_x_features)
    
        for i, j, k in zip(X, runtimes, c):
            ax.scatter(i, j, color=k, s=25, edgecolor='black') 

    else:

        ax.scatter(X, runtimes, c='blue', s=15, label=src)

        if compare:
            ax.scatter(X_compare, runtimes_compare, c='orange', s=15, label=src2)
            plt.legend()

    ax.set_title(X_axis)

    plt.title(f'Run time results {algorithm}', fontdict=fontdict)
    plt.ylabel('Run times in seconds')
    plt.xlabel(X_axis)
    
    fig.tight_layout()

    ## save high quality plot
    if compare:
        fig.savefig(f"plots/with_scale/run_times_{algorithm}_{X_axis}_comparison_not_standardized.pdf", bbox_inches='tight')  
       
    else:
        # fig.savefig(f"plots/with_scale/run_times_{algorithm}_{X_axis}_with_color.pdf", bbox_inches='tight')  
        fig.savefig(f"plots/with_scale/run_times_{algorithm}_{X_axis}_not_standardized.pdf", bbox_inches='tight')   
    plt.show()

    # print(runtimes)
    # print(runtimes_compare)
    # print(f'{algorithm} had {None_values} None values with these datasets:\n {None_datasets}')
    
# algos = 'lap_score', 'low_variance', 'SPEC', 'MCFS', 'NDFS', 'UDFS'
options = 'objects', 'features', 'objects_x_features'

# for algorithm in algos:
#     for option in options:
#         plot_run_times(option, algorithm, compare=False)
# for option in options:
#     for algorithm in ['UDFS', 'NDFS']:
#         plot_run_times(option, algorithm, compare=False, with_color=False)

# plot_datasets(src='results/dataset_params.json')

def plot_run_times_faucet(algorithms, X_axis):
    
    """
    Function that does unsupervised feature selection with laplacian score.

    PARAMS:
    X_axis: what you want the x-axis to represent. 
        The options are features, objects and objects times features.
        The latter you write as objects_x_features 

    algorithm: the algorithm you want analyzed. It automatically grabs the
        right dataset. 

    compare: to potentially compare another file with results
        which were received with running it again in peregrine. 

    ------------
    Returns:

    Nothing, but saves a pdf of the plot in the plots directory.
    """
    fig, axs = plt.subplots(2, 3, sharex=True, figsize=(20, 10))

    for ax, algorithm in zip(axs.flat, algorithms):

            ax.set_title(algorithm)
            
            src = f'results/{algorithm}_peregrine.json'
            # src = f'results/{algorithm}_standardized.json'

            # create a dataframe from a json file 
            with open(src) as json_file:
                data = json.load(json_file)

            runtimes = []
            objects = []
            features = []
            objects_x_features = []
            
            for key, value in data[algorithm].items():

                split_values = re.split(r'(\d+)', key)

                objects.append(int(split_values[1]))
                features.append(int(split_values[3]))
                objects_x_features.append(int(split_values[1]) * int(split_values[3]))

                runtimes.append(float(value['run_time']))


             # get the desired values for the x axis:
             # either features, objects, object_x_features
            X = eval(X_axis)

            if X_axis == 'objects':
                features = [x / 100 for x in features]
                ax.scatter(X, runtimes, s=features)


            elif X_axis == 'features':
                objects = [x / 100 for x in objects]
                ax.scatter(X, runtimes, s=objects)

            else:
                ax.scatter(X, runtimes)

        
    fig.suptitle('Run time results', fontdict=fontdict)
    fig.supxlabel(X_axis[0].upper() + X_axis[1:])
    fig.supylabel('Runtime in seconds')

    fig.tight_layout()
    fig.savefig(f"plots/good_plots/runtimes_bubble_faucet_{X_axis}.pdf", bbox_inches='tight')   

    # plt.show()

    
algos = 'lap_score', 'low_variance', 'SPEC', 'MCFS', 'NDFS', 'UDFS'

plot_run_times_faucet(algos, 'features')
plot_run_times_faucet(algos, 'objects')