"""File that will run the experiment and store the results.

ISSUES:

- Unsure what to do with the n of selected features, 
    same for every algorithm?
    with a value for p in low_variance it's different for example

    See comments in evaluation_functions.py 

    Probably not too relevant however, since we focus on run time not on acc and nmi. 

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
import sys

# importing the functions in which the algorithms are evaluated 
from evaluation_functions_jobscripts import eval_lap_score, eval_low_variance, eval_MCFS, \
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

    TODO: maybe handle Traceback in some way, unsure yet
    """
    signal.alarm(max_run_time) # reset the alarm
    raise TookTooLong

"""  Set global variable for max total run time.

After this time has passed, the analysis will stop and the results saved to a json file.

For the experiment we have ten days: 24*60*60 = 86_400 seconds. 
To be safe set it to 80_000 seconds. 
"""
max_global_time = 80_000

"""  Set global variable for maximal run time per algorithm + dataset

This is the max run time an algorithm is allowed to analyze a certain dataset.

The jobs for NDFS, UDFS and MCFS are split to last 10 days per five datasets.
So with a max run_time of 15_000 seconds per combination it should be more than safe.
"""

max_run_time = 15_000

def main():
    global_start_time = time()

    print(f'\n---The parameters passed to python file: \n {sys.argv}')
    try: 
        algorithm, idx_min, num_datasets = sys.argv[1:]
    
    except ValueError as e:
        print(f'\nPass parameters as follows: python script.py algorithm idx_min, idx_max \nPython Exception: ValueError: {e}')
        return 

    # list of a couple random datasets to evaluate to quickly test things if needed.
    # datasets = ['data/COIL20.mat', 'data/Isolet.mat', 'data/Yale.mat', 'data/ORL.mat']

    """ The lines below are to run datatsets in directories

    os.listdir generates an unordered list of all the files, the endswith make sure
    that only files with the .mat extension are kept. 
    """
    # set the directory for the data to be analyzed
    data_directory = 'synthetic_data/'

    # os.listdir generates an unordered list of all the files, the endswith make sure
    # that only files with the .mat extension are appended to the list 
    datasets = [f for f in os.listdir(data_directory) if f.endswith('.mat')]
    
    # potentially set limit to number of datasets with args passed to script
    datasets = datasets[int(idx_min):int(idx_min)+int(num_datasets)] 

    # Set an alarm per line of code with the max_run_time variable defined before 
    signal.signal(signal.SIGALRM, handler)
    signal.alarm(max_run_time)

    # parameters for contruct_W function 
    W_kwargs = {"metric": "euclidean", "neighborMode": "knn", "weightMode": "heatKernel", "k": 5, 't': 1}
    
    # number of features to select
    num_features = 100

    # dictionary to store results 
    results = {algorithm:{}}

    for idx, dataset in enumerate(datasets):

        # if more than global time has passed, quit the analysis and save results.
        if time() - global_start_time > max_global_time: 
            print('\nMax_global_time exceeded. Writing obtained results to json file.')

            # save the datasets that were not analyzed to know which ones are missing
            # TODO: function does not work well yet. It is the case that they are not 
            #       updated accordingly when it's rewritten to the file. 
            #       However, this function was not needed with the first peregrine test.

            results['not analyzed datasets bc max_global_time'] = datasets[idx:]
            break 
        
        print(f'\n----Time passed----\n{round(time()-global_start_time)} sec')
        print('\nDataset to be analyzed:', str(dataset))

        # load the dataset from the data_directory
        mat = scipy.io.loadmat(data_directory + dataset)
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
        """

        # first try to run it normally
        # NOTE: for some methods, we run it with unrequired params to improve readability
        try:     
            # construct a string with all the arguments that evaluated with built-in eval() function

            nmi, acc, run_time = eval('eval_' + algorithm + '(X, y, num_clusters, num_features, W_kwargs)')

        # if it takes too long (set in handler()), set None to all three outputs
        except TookTooLong:     
            nmi, acc, run_time = None, None, None

        # always add the nmi, acc and run time to the results dictionary  
        finally: 
            results[algorithm].update({dataset:{'nmi':nmi, 'acc':acc, 'run_time':run_time}})


    """
    Convert dictionary to json file and save it.

    First create filename which is specific for algoritm to keep things ordered.

    Then see if file already exists, which happens when a previous job already wrote 
    to the file. Get the data of the already existing file and update the results in 
    current job with it. Then overwrite all the results to the file. 

    It is not the most efficient way, but it is clear and easy to use afterwards.

    Warning: update causes the values in results are overwritten.
    """

    filename = f'results/{algorithm}_peregrine.json'

    if os.path.exists(filename):
        with open(filename, 'r') as f:
            data = json.load(f)

        # add every newly analyzed dataset to the data
        for key in results[algorithm].keys():
            if key not in data[algorithm].keys():
                data[algorithm][key] = results[algorithm][key]       
                
        with open(filename, 'w') as f:
            json.dump(data, f, indent=4)

    else:
        # add parameters to later save to json file
        results['num_features'] = num_features
        results['W_kwargs'] = W_kwargs
        results['max_run_time'] = max_run_time
        results['max_global_time'] = max_global_time

        with open(filename, 'w') as f:
            json.dump(results, f, indent=4)

    print('\n\nWritten to json file.')



if __name__ == '__main__':
    main()