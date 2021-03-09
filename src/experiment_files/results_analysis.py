""" 

File that plots the results obtained in experiment_config.py

"""

import pandas as pd
import matplotlib.pyplot as plt
import re 
from seaborn import set_theme
set_theme() #sets a nice plotting style for all the plot in the script

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


def plot_run_times(src='results/results_small_datasets_30sec.json'):
    """
    Function that plots the run times. 

    Takes in the path of a json file containing results generated
    by experiment_config.py

    Returns nothing, but saves a pdf of a plot
    """

    # create a dataframe from a json file 
    df = pd.read_json(src)

    # remove the columns and rows that carry parameter information
    df = df.drop(['num_features', 'W_kwargs', 'max_run_time'], axis=1) # coolumns
    df = df.drop(['metric', 'k', 't', 'weightMode', 'neighborMode']) # rows

    # get a list of the column names
    lissie = list(df)

    for column_name in lissie:
        """
        Here, the dataframe still contains a dictionary for every element.
        This dictionary contains the nmi, acc and run time. With the following line 
        we extract the run time information and replac the dictionary with it 
        """
        df[column_name] = df[column_name].apply(get_run_time)

    # get list with the size of the dataset (features * samples)
    sizes = [] 
    for dataset in df.index.values: # iterate over every row in dataframe

        # the line below splits letters and other characters from numbers
        split_values = re.split(r'(\d+)', dataset) 

        # multiply the samples (split_values[1]) with the features (split_values[3])
        size = float(split_values[1]) * float(split_values[3]) 

        sizes.append(int(size))

    fig, ax = plt.subplots()

    # add the results per algorithm, so per column 
    for column_name in list(df):
        ax.scatter(x=sizes, y=df[column_name], label=column_name)

    plt.title('Run time results', fontdict=fontdict)
    plt.xlabel('features * samples')
    plt.ylabel('Run time')
    plt.legend()
    fig.savefig("results/run_times.pdf", bbox_inches='tight') ## save high quality plot 


def get_run_time(dictionary):
    """
    Function that converts the dataframe elements in the results to only the run time result
    """
    return  dictionary['run_time']


plot_run_times(src='results/results_small_datasets_30sec.json')

plot_datasets(src='results/dataset_params.json')