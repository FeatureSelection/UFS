"""
Script that contains the function with which the algorithms are analyzed. 
jobscripts_config.py imports these functions to analyze datasets and collect data.

This version is different to evaluation functions.py in multiple ways.

1) This file gets the input arrays X and y from the configuration file.
    In evaluation_functions.py the datasets are loaded for every function 

2) To improve readability, all functions take in all the parameters. 
    This makes it much neater in jobscripts_config,py
    It is not the prettiest soltuion, might wnt to make it clearer later

"""


# Often used imports
from time import time
import numpy as np 
import scipy.io
from skfeature.utility import construct_W
from skfeature.utility import unsupervised_evaluation
from skfeature.utility.sparse_learning import feature_ranking


from skfeature.function.similarity_based import lap_score

def eval_lap_score(X, y, num_clusters, num_features, W_kwargs):
    """
    Function that does unsupervised feature selection with laplacian score.

    PARAMS:
    X: data to analyze
    y: true labels 
    num_clusters,: number of clusters in ground truth of dataset
    W_kwargs: parameters for construct_W function

    ------------
    Returns:
    The nmi, acc and run time

    """

    # start time after dataset has been loaded, loading shouldn't affect runtime
    start_time = time()

    # construct affinity matrix
    W = construct_W.construct_W(X, **W_kwargs)

    # obtain the scores of features
    score = lap_score.lap_score(X, W=W)

    # sort the feature scores in an ascending order according to the feature scores
    idx = lap_score.feature_ranking(score)

    # get run time for lap_score algorithm
    run_time = time() - start_time

    # obtain the dataset on the selected features
    selected_features = X[:, idx[0:num_features]]
    
    # perform kmeans clustering based on the selected features and repeats 20 times
    nmi_total = 0
    acc_total = 0
    for _ in range(0, 20):
        nmi, acc = unsupervised_evaluation.evaluation(X_selected=selected_features, n_clusters=num_clusters, y=y)
        nmi_total += nmi
        acc_total += acc

    # get the averages
    nmi = float(nmi_total)/20
    acc = float(acc_total)/20

    # output nmi, acc and run time 
    return nmi, acc, run_time 


from skfeature.function.statistical_based import low_variance

def eval_low_variance(X, y, num_clusters, num_features, W_kwargs):
    """
    Function that does unsupervised feature selection with low variance analysis.

    PARAMS:
    X: data to analyze
    y: true labels 
    num_clusters,: number of clusters in ground truth of dataset
    W_kwargs: parameters for construc_W function

    ------------
    Returns:
    The nmi, acc and run time

    """ 

    # start time after dataset has been loaded, loading shouldn't affect runtime
    start_time = time()

    p = 0.1    # specify the threshold p to be 0.1

    """
    perform feature selection and obtain the dataset on the selected features
    The selected features work differently here. In other algorithms we select the best 
    X number of features, whereas here we select with minimal variance which leads to 
    differing number of feature selected. Might be an advantage for nmi and acc?
    Not important for run time however. 
    """

    # perform feature selection and obtain the dataset on the selected features
    selected_features = low_variance.low_variance_feature_selection(X, p*(1-p))

    # get run time for low_variance algorithm
    run_time = time() - start_time

    # perform kmeans clustering based on the selected features and repeats 20 times
    nmi_total = 0
    acc_total = 0

    for _ in range(0, 20): 
        nmi, acc = unsupervised_evaluation.evaluation(X_selected=selected_features, n_clusters=num_clusters, y=y)
        nmi_total += nmi
        acc_total += acc 

    # get the averages
    nmi = float(nmi_total)/20
    acc = float(acc_total)/20

    # output nmi, acc and run time 
    return nmi, acc, run_time 

    # line below is for multiprocessing to keep track of which combination the results describe
    # return 'low_variance', 'dataset', run_time




from skfeature.function.sparse_learning_based import MCFS

def eval_MCFS(X, y, num_clusters, num_features, W_kwargs):
    """
    Function that does unsupervised feature selection with MCFS algorithm.

    PARAMS:
    X: data to analyze
    y: true labels 
    num_clusters,: number of clusters in ground truth of dataset
    W_kwargs: parameters for construc_W function

    ------------
    Returns:
    The nmi, acc and run time

    """

    # start time after dataset has been loaded, loading shouldn't affect runtime
    start_time = time()

    # construct affinity matrix
    W = construct_W.construct_W(X, **W_kwargs)

    # obtain the feature weight matrix
    Weight = MCFS.mcfs(X, n_selected_features=num_features, W=W, n_clusters=20)

    # sort the feature scores in an ascending order according to the feature scores
    idx = MCFS.feature_ranking(Weight)

    # get run time for MCFS algorithm
    run_time = time() - start_time

    # obtain the dataset on the selected features
    selected_features = X[:, idx[0:num_features]]

    # perform kmeans clustering based on the selected features and repeats 20 times
    nmi_total = 0
    acc_total = 0
    for _ in range(0, 20):
        nmi, acc = unsupervised_evaluation.evaluation(X_selected=selected_features, n_clusters=num_clusters, y=y)
        nmi_total += nmi
        acc_total += acc

    # get the averages
    nmi = float(nmi_total)/20
    acc = float(acc_total)/20

    # output nmi, acc and run time 
    return nmi, acc, run_time 



from skfeature.function.sparse_learning_based import NDFS
from skfeature.utility.sparse_learning import feature_ranking

def eval_NDFS(X, y, num_clusters, num_features, W_kwargs):
    """
    Function that does unsupervised feature selection with NDFS algorithm.

    PARAMS:
    X: data to analyze
    y: true labels 
    num_clusters,: number of clusters in ground truth of dataset
    W_kwargs: parameters for construc_W function

    ------------
    Returns:
    The nmi, acc and run time

    """
    
    # start time after dataset has been loaded, loading shouldn't affect runtime
    start_time = time()
    
    # construct affinity matrix
    W = construct_W.construct_W(X, **W_kwargs)

    # obtain the feature weight matrix
    Weight = NDFS.ndfs(X, W=W, n_clusters=20)

    # sort the feature scores in an ascending order according to the feature scores
    idx = feature_ranking(Weight)

    # get run time for NDFS algorithm
    run_time = time() - start_time

    # obtain the dataset on the selected features
    selected_features = X[:, idx[0:num_features]]

    # perform kmeans clustering based on the selected features and repeats 20 times
    nmi_total = 0
    acc_total = 0
    for _ in range(0, 20):
        nmi, acc = unsupervised_evaluation.evaluation(X_selected=selected_features, n_clusters=num_clusters, y=y)
        nmi_total += nmi
        acc_total += acc

    # get the averages
    nmi = float(nmi_total)/20
    acc = float(acc_total)/20

    # output nmi, acc and run time 
    return nmi, acc, run_time 



from skfeature.function.similarity_based import SPEC

def eval_SPEC(X, y, num_clusters, num_features, W_kwargs):
    """
    Function that does unsupervised feature selection with SPEC algorithm.

    PARAMS:
    X: data to analyze
    y: true labels 
    num_clusters,: number of clusters in ground truth of dataset
    W_kwargs: parameters for construc_W function

    ------------
    Returns:
    The nmi, acc and run time

    """

    # start time after dataset has been loaded, loading shouldn't affect runtime
    start_time = time()

    # specify the second ranking function which uses all except the 1st eigenvalue
    kwargs = {'style': 0}

    # obtain the scores of features
    score = SPEC.spec(X, **kwargs)

    # sort the feature scores in an descending order according to the feature scores
    idx = SPEC.feature_ranking(score, **kwargs)

    # get run time for SPEC algorithm
    run_time = time() - start_time

    # obtain the dataset on the selected features
    selected_features = X[:, idx[0:num_features]]

    # perform kmeans clustering based on the selected features and repeats 20 times
    nmi_total = 0
    acc_total = 0
    for _ in range(0, 20):
        nmi, acc = unsupervised_evaluation.evaluation(X_selected=selected_features, n_clusters=num_clusters, y=y)
        nmi_total += nmi
        acc_total += acc

    # get the averages
    nmi = float(nmi_total)/20
    acc = float(acc_total)/20

    # output nmi, acc and run time 
    return nmi, acc, run_time 



from skfeature.function.sparse_learning_based import UDFS

def eval_UDFS(X, y, num_clusters, num_features, W_kwargs):
    """
    Function that does unsupervised feature selection with UDFS algorithm.

    PARAMS:
    X: data to analyze
    y: true labels 
    num_clusters,: number of clusters in ground truth of dataset
    W_kwargs: parameters for construc_W function

    ------------
    Returns:
    The nmi, acc and run time

    """

    # start time after dataset has been loaded, loading shouldn't affect runtime
    start_time = time()

    # obtain the feature weight matrix
    Weight = UDFS.udfs(X, gamma=0.1, n_clusters=num_clusters)

    # sort the feature scores in an ascending order according to the feature scores
    idx = feature_ranking(Weight)

    # get run time for NDFS algorithm
    run_time = time() - start_time

    # obtain the dataset on the selected features
    selected_features = X[:, idx[0:num_features]]

    # perform kmeans clustering based on the selected features and repeats 20 times
    nmi_total = 0
    acc_total = 0
    for _ in range(0, 20):
        nmi, acc = unsupervised_evaluation.evaluation(X_selected=selected_features, n_clusters=num_clusters, y=y)
        nmi_total += nmi
        acc_total += acc

    # get the averages
    nmi = float(nmi_total)/20
    acc = float(acc_total)/20

    # output nmi, acc and run time 
    return nmi, acc, run_time 
