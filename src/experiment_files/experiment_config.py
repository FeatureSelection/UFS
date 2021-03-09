"""File that will run the experiment and store the results.

ISSUES:

- Unsure what to do with the n of soelected features, 
    same for every algorithm?
    with a value for p in low_variance it's different for example

    See comments in evaluation_functions.py 

    Might not be too relevant however, since we focus on run time not on acc and nmi. 

"""

# importing necessary tools
import scipy.io 
from skfeature.utility import construct_W, unsupervised_evaluation
from skfeature.utility.sparse_learning import feature_ranking
from time import time
import numpy as np
import json
import os 
import signal 

# importing the functions in which the algorithms are evaluated 
from evaluation_functions import eval_lap_score, eval_low_variance, eval_MCFS, \
                                    eval_NDFS, eval_SPEC, eval_UDFS

class TookTooLong(Exception):
    """
    create a special exception for the combination of algorithm and dataset 
    taking too much time.

    Does not take any arguments and has no output either. 
    """

    def __init__(self):
        pass

def handler(signum, frame):
    """
    Function that handles the signal thrown if max_run_time has been surpassed
    for one line. This one line will always be the execution of an algorithm on 
    a dataset.
    """
    signal.alarm(max_run_time) # reset the alarm
    raise TookTooLong

# set global variable for maximal run time 
max_run_time = 30

def main():
    # list of a couple random datasets to evaluate
    # datasets = ['data/COIL20.mat', 'data/Isolet.mat', 'data/Yale.mat', 'data/ORL.mat']

    """ The lines below are to run datatsets in directories

    os.listdir generates an unordered list of all the files, the endswith make sure
    that only files with the .mat extension are kept. 
    """
    # datasets = ['data/'+ str(f)  for f in os.listdir('data')] 
    # datasets = ['synthetic_data/'+ str(f) for f in os.listdir('synthetic_data') if f.endswith('.mat')]
    datasets = ['small_synthetic_data/'+ str(f) for f in os.listdir('small_synthetic_data') if f.endswith('.mat')]

    signal.signal(signal.SIGALRM, handler)
    signal.alarm(max_run_time)

    # parameters for contruct_W function 
    W_kwargs = {"metric": "euclidean", "neighborMode": "knn", "weightMode": "heatKernel", "k": 5, 't': 1}
    
    # number of features to select
    num_features = 100

    # dictionary to store results 
    results = {'low_variance':{}, 'lap_score':{}, 'MCFS':{}, 'NDFS':{}, 'SPEC':{}, 'UDFS':{}}

    # add global parameters to later save to json file
    results['num_features'] = num_features
    results['W_kwargs'] = W_kwargs
    results['max_run_time'] = max_run_time

    for dataset in datasets:
        print('\n\nDataset:', str(dataset))
        mat = scipy.io.loadmat(dataset) # in .../scikit-feature-master/skfeature/data
        X = mat['X']    # data
        X = X.astype(float)
        y = mat['Y']    # label
        y = y[:, 0]

        num_clusters = len(np.unique(y)) # n of classes in ground truth 

        """ Next section evaluates and stores the results for every algorithm

        Here, if the eval function takes longer than the max_run_time (set in 
        just before defining main()), an TookTooLong exception will be thrown.
        The results of nmi, acc, and run_time will be set to none and the script
        will continue at the next line. 

        First it is elaborately shown how it works, then the same procedure happens
        but the code is more compact. 
        """

        # first try to run it normally
        try:     
            nmi, acc, run_time = eval_low_variance(X, y, num_clusters)

        # if it takes too long (set in handler()), get None for all three outputs
        except TookTooLong:     
            nmi, acc, run_time = None, None, None

        # always add the nmi, acc and run time to the results dictionary  
        # TODO: maybe change dataset name to not include the synthetic_data/ direcotry label    
        finally: 
            results['low_variance'].update({dataset:{'nmi':nmi, 'acc':acc, 'run_time':run_time}})

        # the following code is the same as previously decribed but more succinct for the remaining algorithms.

        try:                nmi, acc, run_time = eval_lap_score(X, y, num_clusters, num_features, W_kwargs)
        except TookTooLong: nmi, acc, run_time = None, None, None
        finally:            results['lap_score'].update({dataset:{'nmi':nmi, 'acc':acc, 'run_time':run_time}})

        try:                nmi, acc, run_time = eval_MCFS(X, y, num_clusters, num_features, W_kwargs)
        except TookTooLong: nmi, acc, run_time = None, None, None
        finally:            results['MCFS'].update({dataset:{'nmi':nmi, 'acc':acc, 'run_time':run_time}})     
        
        try:                nmi, acc, run_time = eval_NDFS(X, y, num_clusters, num_features, W_kwargs)
        except TookTooLong: nmi, acc, run_time = None, None, None
        finally:            results['NDFS'].update({dataset:{'nmi':nmi, 'acc':acc, 'run_time':run_time}})

        try:                nmi, acc, run_time = eval_SPEC(X, y, num_clusters, num_features)
        except TookTooLong: nmi, acc, run_time = None, None, None
        finally:            results['SPEC'].update({dataset:{'nmi':nmi, 'acc':acc, 'run_time':run_time}})

        try:                nmi, acc, run_time = eval_UDFS(X, y, num_clusters, num_features)
        except TookTooLong: nmi, acc, run_time = None, None, None
        finally:            results['UDFS'].update({dataset:{'nmi':nmi, 'acc':acc, 'run_time':run_time}})

    # convert dictionary to json file and save it 
    with open('results/results_small_datasets_30sec.json', 'w') as fp:
        json.dump(results, fp, indent=4)
        print('\n\nWritten to json file.')


if __name__ == '__main__':
    main()