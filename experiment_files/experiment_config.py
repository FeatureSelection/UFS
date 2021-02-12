"""
File that will run the experiment and store the results.
"""

# importing necessary tools
import scipy.io 
from skfeature.utility import construct_W, unsupervised_evaluation
from skfeature.utility.sparse_learning import feature_ranking
from time import time
import numpy as np
import json
import os 

# importing the functions in which the algorithms are evaluated 
from evaluation_functions import eval_lap_score, eval_low_variance, eval_MCFS, \
                                    eval_NDFS, eval_SPEC, eval_UDFS
                                    

def main():
    # list of datasets to evaluate
    datasets = ['data/COIL20.mat', 'data/Isolet.mat', 'data/Yale.mat', 'data/ORL.mat']
    
    # commented out line below is to run all datatsets.
    # datasets = ['data/'+ str(f)  for f in os.listdir('data')] 

    # parameters for contruct_W function 
    W_kwargs = {"metric": "euclidean", "neighborMode": "knn", "weightMode": "heatKernel", "k": 5, 't': 1}
    
    # number of features to select
    num_features = 100

    # dictionary to store results 
    results = {'low_variance':{}, 'lap_score':{}, 'MCFS':{}, 'NDFS':{}, 'SPEC':{}, 'UDFS':{}}

    for dataset in datasets:
        mat = scipy.io.loadmat(dataset) # in .../scikit-feature-master/skfeature/data
        X = mat['X']    # data
        X = X.astype(float)
        y = mat['Y']    # label
        y = y[:, 0]

        num_clusters = len(np.unique(y)) # n of classes in ground truth 

        # evaluate and store the results for every algorithm
        nmi, acc, run_time = eval_low_variance(X, y, num_clusters)
        results['low_variance'].update({dataset:{'nmi':nmi, 'acc':acc, 'run_time':run_time}})

        nmi, acc, run_time = eval_lap_score(X, y, num_clusters, num_features, W_kwargs)
        results['lap_score'].update({dataset:{'nmi':nmi, 'acc':acc, 'run_time':run_time}})

        nmi, acc, run_time = eval_MCFS(X, y, num_clusters, num_features, W_kwargs)
        results['MCFS'].update({dataset:{'nmi':nmi, 'acc':acc, 'run_time':run_time}})

        nmi, acc, run_time = eval_NDFS(X, y, num_clusters, num_features, W_kwargs)
        results['NDFS'].update({dataset:{'nmi':nmi, 'acc':acc, 'run_time':run_time}})

        nmi, acc, run_time = eval_SPEC(X, y, num_clusters, num_features)
        results['SPEC'].update({dataset:{'nmi':nmi, 'acc':acc, 'run_time':run_time}})

        nmi, acc, run_time = eval_UDFS(X, y, num_clusters, num_features)
        results['UDFS'].update({dataset:{'nmi':nmi, 'acc':acc, 'run_time':run_time}})

    # convert dictionary to json file and save it 
    with open('results.json', 'w') as fp:
        json.dump(results, fp, indent=4)

if __name__ == '__main__':
    main()