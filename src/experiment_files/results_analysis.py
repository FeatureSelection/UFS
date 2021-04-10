""" 

File that plots the results obtained in experiment_config.py

"""

import pandas as pd
import matplotlib.pyplot as plt
import re 
import json 
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

def plot_run_times(X_values, algorithm, compare=False):

    """
    Function that does unsupervised feature selection with laplacian score.

    PARAMS:
    X_values: what you want the x-axis to represent. 
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

    # create a dataframe from a json file 
    with open(src) as json_file:
        data = json.load(json_file)

            
    runtimes = []
    objects = []
    features = []
    objects_x_features = []
    None_values = 0
    None_datasets = []

    print(algorithm, len(data[algorithm].items()))
    
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
        src2 = f'results/{algorithm}_peregrine_first_try.json'

        with open(src2) as json_file:
            data_compare = json.load(json_file)

        
        runtimes_compare = []
        for key, value in data_compare[algorithm].items():
            #only runtimes are relevant, datasets are in same order
            # seems to be true, but always be wary!

            runtimes_compare.append(float(value['run_time']))

    # get the desired values for the x axis:
    # either features, objects, object_x_features
    X = eval(X_values)
    
    fig, ax = plt.subplots()
    
    ax.scatter(X, runtimes, c='b', s=15) 
    if compare:
        ax.scatter(X, runtimes_compare, c='orange', s=15)
    ax.set_title(X_values)

    plt.title(f'Run time results {algorithm}', fontdict=fontdict)
    plt.ylabel('Run times in seconds')
    plt.xlabel(X_values)
    
    fig.tight_layout()
    ## save high quality plot
    if compare:
        fig.savefig(f"plots/run_times_{algorithm}_{X_values}_comparison.pdf", bbox_inches='tight')  
       
    else:
        fig.savefig(f"plots/run_times_{algorithm}_{X_values}.pdf", bbox_inches='tight')  

    # print(f'{algorithm} had {None_values} None values with these datasets:\n {None_datasets}')
    
algos = 'lap_score', 'low_variance', 'SPEC', 'MCFS', 'NDFS', 'UDFS'
options = 'objects', 'features', 'objects_x_features'

for algorithm in algos:
    for option in options:
        plot_run_times(option, algorithm, compare=False)

# plot_datasets(src='results/dataset_params.json')
