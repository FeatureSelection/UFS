"""File that will run the experiment and store the results.

ISSUES:

- Unsure what to do with the n of selected features, 
    same for every method?
    with a value for p in low_variance it's different for example

    See comments in evaluation_functions.py 

    Probably not too relevant however, since we focus on run time not on acc and nmi. 
    Only has a minor influence on runtime most likely, judging by prediction accuracy 
    and time complexities SPEC and MCFS 
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

# importing the functions in which the methods are evaluated 
from evaluation_functions_jobscripts import eval_lap_score, eval_low_variance, eval_MCFS, \
                                    eval_NDFS, eval_SPEC, eval_UDFS

class TookTooLong(Exception):
    """
    create a special exception for the combination of method and dataset 
    taking too much time.

    Does not take any arguments and has no output either. 
    """

    def __init__(self):
        pass

def handler(signum, frame):
    """
    Function that handles the signal thrown if max_runtime has been surpassed
    for one line. This one line will always be the execution of an method on 
    a dataset.

    TODO: maybe handle Traceback in some way, unsure yet
    """
    signal.alarm(max_runtime) # reset the alarm
    raise TookTooLong

"""  Set global variable for max total run time.

After this time has passed, the analysis will stop and the results saved to a json file.

For the experiment we have ten days: 10*24*60*60 = 864_000 seconds. 
NOTE: this must be changed to match the #SBATCH --
To be safe set it to 850_000 seconds. 
"""
max_global_time = 850_000

"""  Set global variable for maximal run time per method + dataset

This is the max run time an method is allowed to analyze a certain dataset.

The jobs for NDFS, UDFS and MCFS are split to last 10 days per five datasets.
So with a max runtime of 15_000 seconds per combination it should be more than safe.
"""
# because batches of three
max_runtime = 300_000

def main():
    global_start_time = time()

    print(f'\n---The parameters passed to python file: \n {sys.argv}')
    try: 
        method, idx_min, num_datasets = sys.argv[1:]
    
    except ValueError as e:
        print(f'\nPass parameters as follows: python script.py method idx_min, idx_max \nPython Exception: ValueError: {e}')
        return 

    # list of a couple random datasets to evaluate to quickly test things if needed.
    # datasets = ['data/COIL20.mat', 'data/Isolet.mat', 'data/Yale.mat', 'data/ORL.mat']

    """ The lines below are to run datatsets in directories

    os.listdir generates an unordered list of all the files, the endswith make sure
    that only files with the .mat extension are kept. When one job is submitted in which   
    this script can be called with different arguments, it has the same order so every dataset
    is analyzed. Further research is needed to figure how the randomizaiton works. 
    """
    # set the directory for the data to be analyzed
    data_directory = 'synthetic_data/'
    # data_directory = 'real_data/'

    # os.listdir generates an unordered list of all the files, the endswith make sure
    # that only files with the .mat extension are appended to the list 
    datasets = [f for f in os.listdir(data_directory) if f.endswith('.mat')]

    # potentially set limit to number of datasets with args passed to script
    idx_min = int(idx_min)
    if idx_min + int(num_datasets) <= len(datasets):
        idx_max = idx_min + int(num_datasets)
    else:
        idx_max = len(datasets)

    datasets = datasets[idx_min:idx_max] 

    # Set an alarm per line of code with the max_runtime variable defined before 
    signal.signal(signal.SIGALRM, handler)
    signal.alarm(max_runtime)

    # parameters for contruct_W function 
    W_kwargs = {"metric": "euclidean", "neighborMode": "knn", "weightMode": "heatKernel", "k": 5, 't': 1}
    
    # number of features to select
    num_features = 100

    # dictionary to store results 
    results = {method:{}}

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
        X_raw = mat['X']    # data
        X_raw = X_raw.astype(float)
        
        # standardize data according to Dy & Brodley (2004): mean = 0 and std = 1
        X = (X_raw - X_raw.mean()) / X_raw.std()

        y = mat['Y']    # label
        y = y[:, 0]

        num_clusters = len(np.unique(y)) # n of classes in ground truth 

        """ Next section evaluates and stores the results for every method

        Here, if the eval function takes longer than the max_runtime (set in 
        just before defining main()), an TookTooLong exception will be thrown.
        The results of nmi, acc, and runtime will be set to none and the script
        will continue at the next line. 
        """

        # first try to run it normally
        # NOTE: for some methods, we run it with unrequired params to improve readability
        try:     
            # construct a string with all the arguments that evaluated with built-in eval() function

            nmi, acc, runtime = eval('eval_' + method + '(X, y, num_clusters, num_features, W_kwargs)')

        # if it takes too long (set in handler()), set None to all three outputs
        except TookTooLong:     
            nmi, acc, runtime = None, None, None

        # always add the nmi, acc and run time to the results dictionary  
        finally: 
            results[method].update({dataset:{'nmi':nmi, 'acc':acc, 'runtime':runtime}})


    """
    Convert dictionary to json file and save it.

    First create filename which is specific for algoritm to keep things ordered.

    Then see if file already exists, which happens when a previous job already wrote 
    to the file. Get the data of the already existing file and update the results in 
    current job with it. Then overwrite all the results to the file. 

    It is not the most efficient way, but it is clear and easy to use afterwards.

    Warning: update causes the values in results are overwritten.
    """

    # filename = f'results/{method}_real_world.json'   
    filename = f'results/{method}_synthetic_confirm_outliers.json'

    if os.path.exists(filename):
        with open(filename, 'r') as fp:
            data = json.load(fp)

        # add every newly analyzed dataset to the data
        for key in results[method].keys():
            if key not in data[method].keys():
                data[method][key] = results[method][key]       
                
        with open(filename, 'w') as fp:
            json.dump(data, fp, indent=4)

    else:
        # add parameters to later save to json file
        results['num_features'] = num_features
        results['W_kwargs'] = W_kwargs
        results['max_runtime'] = max_runtime
        results['max_global_time'] = max_global_time

        with open(filename, 'w') as fp:
            json.dump(results, fp, indent=4)

    print('\n\nWritten to json file.')


if __name__ == '__main__':
    main()