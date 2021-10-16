""" Generating synthetic datasets

This script generates datasets roughly similar to the ones in scikit-feature. 

This script is used to support prediction of datasets in two ways. 

1) It increases the number of datasets and thus more data to predict on.
2) It can fill in the gaps of data where we do not have real datasets for. 

Lastly, with the real life datasets we can see whether the prediction based on 
synthetic datasets is correct. 
"""

from sklearn.datasets import make_classification
from random import randint, uniform
import scipy.io
import json

dataset_params = {}

# the following parameters you can change yourself, with these
# parameters I generated a few small test datasets
min_features = 100
max_features = 1000

min_samples = 100
max_samples = 1000

step_size = 200

for n_features in range(step_size, max_features, step_size):
    for n_samples in range(step_size, max_samples, step_size):
        if n_samples * n_features > 10_000_000: # to avoid very big datasets. 
            break
        
        dataset_name = 'DS' + str(n_samples) + 'x' + str(n_features) +'.mat'
        # generate a random number of informative features which is lower than 0.5*n_features
        n_informative = randint(2, int(n_features/2))
        # generate a random number of redundant features 
        n_redundant = randint(5, int(n_features/2))
        # have no duplicate features
        # not likely to have the same column in real life dataset (true? still check some time)
        n_repeated = 0

        """
        the next parameters are not important for run time, 
        # only for acc and nmi. They are set to mimic real life datasets. 
        """

        # generate a random number of clusters per class label
        n_clusters_per_class = randint(1, 3)

        # generate a random number of classes
        # ValueError: n_classes(13) * n_clusters_per_class(2) must be smaller or equal 2**n_informative(4)=16
        if 2**n_informative * n_clusters_per_class < 30:
            n_classes = randint(2, int(2**n_informative/n_clusters_per_class))
        else:
            n_classes = randint(2, 30)

        # get a percentage of samples that is randomly allocated to a class
        # it is set to a random float between 0.01 (little noise) and 0.5 (high noise). 
        flip_y = uniform(0.01, 0.5)

        # set a class separation parameter, the bigger the easier the clustering task
        class_sep = uniform(0.5, 2.5) # NOTE: this is not really based on anything

        X, y = make_classification(n_samples=n_samples, n_features=n_features, n_informative=n_informative, 
                                    n_redundant=n_redundant, n_repeated=n_repeated, n_classes=n_classes,  
                                    n_clusters_per_class=n_clusters_per_class, flip_y=flip_y, class_sep=class_sep)

        # to change shape y from (len(y), ) to (len(y), 1)
        # this must be done to allow analysis in experiment_config.py
        y = y[:, None]

        scipy.io.savemat('mimick_data/' + dataset_name +'.mat', {'X':X, 'Y':y})
        dataset_params[dataset_name] = {'n_samples':n_samples, 'n_features':n_features, 'n_informative':n_informative, 
                                    'n_redundant':n_redundant, 'n_repeated':n_repeated, 'n_classes':n_classes, 
                                    'n_clusters_per_class':n_clusters_per_class, 'flip_y':flip_y, 'class_sep':class_sep}

        X, y = None, None

# convert dictionary to json file and save it 
with open('results/mimick_data_params.json', 'w') as fp:
    json.dump(dataset_params, fp, indent=4)
    print('Written to JSON file.')