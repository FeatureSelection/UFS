""" This file constructs the dataset parameters
    file which contains the number of objects and features of the real world dattasets. 
"""

import json 
import numpy as np 
import scipy.io
import os

data_directory = 'real_data/'

# os.listdir generates an unordered list of all the files, the endswith make sure
# that only files with the .mat extension are appended to the list 
datasets = [f for f in os.listdir(data_directory) if f.endswith('.mat')]

dataset_params = {}

for dataset in datasets:

    # load the dataset from the data_directory
    mat = scipy.io.loadmat(data_directory + dataset)
    X = mat['X']    # data
    dataset_params.update({dataset:{'objects':X.shape[0], 'features':X.shape[1]}})

with open('results/real_world_dataset_params.json', 'w') as f:
    json.dump(dataset_params, f, indent=4)

    print('Written to json')