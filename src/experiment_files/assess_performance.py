""" 
NOTE: UDFS and NDFS still lack two results due to memory error

NOTE: squared=False in sklearn.metrics.mean_squared_error
 to use RMSE

NOTE: all the way below is to format table (rounding numbers etc)

NOTE: This error:
  making_models.py:28: RuntimeWarning: overflow encountered in exp
  return b1 * np.exp(b2 * x1) + b3 * x2

  Do not believe it is too bad, the number seems to be approximated anyways. 
  Fix is better of course. Last time I did not even get it, so maybe fix unnecessary. 
"""

import json 
import numpy as np

from sklearn import linear_model
from sklearn.model_selection import cross_validate
from scipy.optimize import curve_fit
from sklearn.metrics import mean_absolute_error, mean_squared_error


def power_model(X, b1, b2, b3):
    """ NOTE:
    These functions have no intercept
    """
    x1, x2 = X
    return b1 * np.power(x1, b2) * np.power(x2, b3)


def exponential_linear(X, b1, b2, b3):
    """ NOTE:
    These functions have no intercept
    """
    x1, x2 = X
    # return b1 * np.exp(b2 * x1) + b3 * x2
    return b1 * np.exp(b2 * x1) * (b3 * x2)

def model_evaluation(methods):

    results = {
        'low_variance':{'single_linear': {}, 'multiple_linear':{}, 'power_model':{},
            'exponential_linear': {'x1=objects':{}, 'x1=features': {}}},

        'lap_score':{'single_linear': {}, 'multiple_linear':{}, 'power_model':{}, 
            'exponential_linear': {'x1=objects':{}, 'x1=features': {}}}, 

        'SPEC':{'single_linear': {}, 'multiple_linear':{}, 'power_model':{}, 
            'exponential_linear': {'x1=objects':{}, 'x1=features': {}}},

        'MCFS':{'single_linear': {}, 'multiple_linear':{}, 'power_model':{}, 
            'exponential_linear': {'x1=objects':{}, 'x1=features': {}}, 'outliers': {}}, 

        'UDFS':{'single_linear': {}, 'multiple_linear':{}, 'power_model':{}, 
            'exponential_linear': {'x1=objects':{}, 'x1=features': {}}},

        'NDFS':{'single_linear': {}, 'multiple_linear':{}, 'power_model':{},
            'exponential_linear': {'x1=objects':{}, 'x1=features': {}}, 'outliers': {}}
        }        
    
    # load the parameters of the synthetic datasets
    src1 = f'results/synthetic_dataset_params.json'   
    with open(src1, 'r') as fp:
        synthetic_dataset_params = json.load(fp)

    for method in methods:   
        print(method)         
        src2 = f'results/synthetic_results/{method}_synthetic.json'

        with open(src2, 'r') as fp:
            synthetic_runtimes = json.load(fp)

        runtimes = []
        objects = []
        features = []
        objects_x_features = []
        
        for dataset, outcome in synthetic_runtimes[method].items():

            num_objects = synthetic_dataset_params[dataset]['n_objects']
            num_features = synthetic_dataset_params[dataset]['n_features']
            num_objects_x_features = num_objects * num_features

            runtime = outcome['runtime']

            # filter out outliers and if outlier, save to result dict
            if method == 'NDFS' and num_features > 3000 and runtime < 2000:
                results['NDFS']['outliers'].update({dataset:runtime})
                continue

            if method == 'MCFS' and runtime > 600:
                results['MCFS']['outliers'].update({dataset:runtime})
                continue 

            if method == 'MCFS' and num_objects == 4000 and runtime > 150:
                results['MCFS']['outliers'].update({dataset:runtime})
                continue

            objects.append(num_objects)
            features.append(num_features)
            objects_x_features.append(num_objects_x_features)

            runtimes.append(runtime)

        # convert everything to numpy arrays
        objects = np.asarray(objects)        
        features = np.asarray(features)
        object_x_features = np.asarray(objects_x_features)

        #no random state needed for lingress and scipy curve_fit
        regressor = linear_model.LinearRegression(positive=True, fit_intercept=False)
        # need to reshape for linear regression, see docs
        X = object_x_features.reshape(-1, 1) 

        cv_results_single = cross_validate(regressor, X, y=runtimes, 
                                    cv=10, scoring=['neg_mean_absolute_error', 'neg_root_mean_squared_error'])

        # need to use tolist() because JSON can't handle numpy arrays
        MAE_error = (cv_results_single['test_neg_mean_absolute_error'] * -1).tolist()
        RMSE_error = (cv_results_single['test_neg_root_mean_squared_error'] * -1).tolist()
        results[method]['single_linear'].update({'MAE': MAE_error})
        results[method]['single_linear'].update({'MAE_mean': np.round(np.mean(MAE_error), 3)})

        results[method]['single_linear'].update({'RMSE': RMSE_error})
        results[method]['single_linear'].update({'RMSE_mean': np.round(np.mean(RMSE_error), 3)})
        
        # to get array [(DS1_objects, DS1_features), (DS2_objects, DS2_features), ...]
        X = np.column_stack((objects, features))

        cv_results_multiple = cross_validate(regressor, X, y=runtimes, 
                                    cv=10, scoring=['neg_mean_absolute_error', 'neg_root_mean_squared_error'])

        # change signs to postive, see https://scikit-learn.org/stable/modules/model_evaluation.html
        # need to reshape so it can be serialized in JSON
        MAE_error = (cv_results_multiple['test_neg_mean_absolute_error'] * -1).tolist()
        RMSE_error = (cv_results_multiple['test_neg_root_mean_squared_error'] * -1).tolist()

        results[method]['multiple_linear'].update({'MAE': MAE_error})
        results[method]['multiple_linear'].update({'MAE_mean': np.round(np.mean(MAE_error), 3)})

        results[method]['multiple_linear'].update({'RMSE': RMSE_error})
        results[method]['multiple_linear'].update({'RMSE_mean': np.round(np.mean(RMSE_error), 3)})

        # the number of folds
        cv = 10  
        MAE_folds = []
        RMSE_folds = []

        if method == 'low_variance':
            p0_power = [0.001, 0.000001, 0.001]

        elif method == 'lap_score':
            p0_power = [0.0001, 0.0000001, 0.0001]             
            
        elif method == 'SPEC':
            p0_power = [0.00001, 2, 0.0001]     
            
        elif method == 'MCFS':
            p0_power = [0.00001, 2, 0.001]    
            
        elif method == 'UDFS':
            p0_power = [0.00001, 0.01, 2]
            
        elif method == 'NDFS':
            p0_power = [0.001, 0.01, 2]
        
        for i in range(cv):
            lower = int(len(runtimes) / cv * i)
            upper = int(len(runtimes) / cv * (i+1))

            p_opt, p_cov = curve_fit(power_model, (objects[lower:upper], 
                                    features[lower:upper]), runtimes[lower:upper], p0=p0_power, bounds=(0, np.inf), maxfev=50000)

            predicted = power_model((objects[lower:upper], features[lower:upper]), *p_opt)
            true = runtimes[lower:upper]

            # squared = False for root_mean_squared_error
            MAE_folds.append(mean_absolute_error(true, predicted))
            RMSE_folds.append(mean_squared_error(true, predicted, squared=False))

        results[method]['power_model'].update({'MAE': MAE_folds})
        results[method]['power_model'].update({'MAE_mean': np.round(np.mean(MAE_folds), 3)})

        results[method]['power_model'].update({'RMSE': RMSE_folds})
        results[method]['power_model'].update({'RMSE_mean': np.round(np.mean(RMSE_folds), 3)})
        results[method]['power_model'].update({'p_opt_last_fold':p_opt.tolist()})
        
        # print('\n POWER DONE')
        
        cv = 10 
        MAE_folds = []
        RMSE_folds = []

        #combinations to have both objects and features as x1
        combinations = [(objects, features), (features, objects)]

        if method == 'low_variance':
            # EL stands for Exponential Linear
            p0_EL = [0.01, 0.0001, 0.0001]

        elif method == 'lap_score':
            p0_EL = [0.01, 0.0001, 0.0001]
            
        elif method == 'SPEC':
            p0_EL = [0.01, 0.0001, 0.0001]
            
        elif method == 'MCFS':
            p0_EL = [0.01, 0.0001, 0.0001]
            
        elif method == 'UDFS':
            p0_EL = [0.01, 0.0001, 0.0001]
            
        elif method == 'NDFS':
            p0_EL = [0.01, 0.0001, 0.0001]

        for idx, X in enumerate(combinations):
            MAE_folds = []
            RMSE_folds = []
            
            for i in range(cv):
                lower = int(len(runtimes) / cv * i)
                upper = int(len(runtimes) / cv * (i+1))

                p_opt, p_cov = curve_fit(exponential_linear, (X[0][lower:upper], 
                                        X[1][lower:upper]), runtimes[lower:upper], p0=p0_EL, bounds=(0, np.inf), maxfev=5000)

                predicted = exponential_linear((X[0][lower:upper], X[1][lower:upper]), *p_opt)
                true = runtimes[lower:upper]

                # squared = False for root_mean_squared_error
                MAE_folds.append(mean_absolute_error(true, predicted))
                RMSE_folds.append(mean_squared_error(true, predicted, squared=False))

            if idx == 0:
                option = 'x1=objects'
            else:
                option = 'x1=features'

            results[method]['exponential_linear'][option].update({'MAE': MAE_folds})
            results[method]['exponential_linear'][option].update({'MAE_mean': np.round(np.mean(MAE_folds), 3)})

            results[method]['exponential_linear'][option].update({'RMSE': RMSE_folds})
            results[method]['exponential_linear'][option].update({'RMSE_mean': np.round(np.mean(RMSE_folds), 3)})
            results[method]['exponential_linear'][option].update({'p_opt_last_fold':list(list(p_opt))})

        
    # have STOP here to not accidentally overwrite file
    filename = 'STOP results/model_evaluation.json' 

    with open(filename, 'w') as f:
        json.dump(results, f, indent=4)

        print('\n\nWritten to json file.')
    return 

def best_models(methods):
    
    results = {'low_variance':{},
                'lap_score':{}, 
                'SPEC':{},
                'MCFS':{'outliers': {}},
                'UDFS':{},
                'NDFS':{'outliers': {}} }

    src1 = f'results/synthetic_dataset_params.json'   
    with open(src1, 'r') as fp:
        synthetic_dataset_params = json.load(fp)

    for method in methods:   
        print(method)         
        src2 = f'results/synthetic_results/{method}_synthetic.json'

        with open(src2, 'r') as fp:
            synthetic_runtimes = json.load(fp)

        runtimes = []
        objects = []
        features = []
        objects_x_features = []
        
        for dataset, outcome in synthetic_runtimes[method].items():

            num_objects = synthetic_dataset_params[dataset]['n_objects']
            num_features = synthetic_dataset_params[dataset]['n_features']
            runtime = outcome['runtime']

            # filter out outliers and if outlier, save to result dict
            if method == 'NDFS' and num_features > 3000 and runtime < 2000:
                results['NDFS']['outliers'].update({dataset:runtime})
                continue

            if method == 'MCFS' and runtime > 600:
                results['MCFS']['outliers'].update({dataset:runtime})
                continue 

            if method == 'MCFS' and num_objects == 4000 and runtime > 150:
                results['MCFS']['outliers'].update({dataset:runtime})
                continue

            objects.append(num_objects)
            features.append(num_features)

            runtimes.append(runtime)

        # convert everything to numpy arrays
        objects = np.asarray(objects)        
        features = np.asarray(features)

        print(f'Length of Objects: {len(objects)} Features: {len(features)} Runtimes: {len(runtimes)}')

        # since the power model is among the best for every method, do this for every method
        X = (objects, features)

        p_opt, p_cov = curve_fit(power_model, X, runtimes, p0=[0.0001, 1, 1], bounds=(0, np.inf), maxfev=5000)
        results[method].update({'power_model':{'betas':p_opt.tolist()}})

        # also get the best competing best model for low variance and NDFS

        if method == 'low_variance':
            regressor = linear_model.LinearRegression(fit_intercept=False)
            X = (objects * features).reshape(-1, 1)
            # print(X)

            # need to reshape for linear regression, see scikit-learn docs
            regressor.fit(X, runtimes)

            results['low_variance'].update({'slope': regressor.coef_[0]})

        elif method == 'NDFS':
            X = (features, objects)
            p_opt, p_cov = curve_fit(exponential_linear, X, runtimes, p0=[100, 0.001, 0.01], bounds=(0, np.inf), maxfev=5000)
            results['NDFS'].update({'exponential_linear_features':{'betas': p_opt.tolist() }})

        elif method == 'UDFS':
            X = (features, objects)
            p_opt, p_cov = curve_fit(exponential_linear, X, runtimes, p0=[100, 0.001, 0.01], bounds=(0, np.inf), maxfev=5000)
            results['UDFS'].update({'exponential_linear_features':{'betas': p_opt.tolist() }})
            
    # have STOP here to not accidentally overwrite file
    filename = 'results/best_models.json'

    with open(filename, 'w') as f:
        json.dump(results, f, indent=4)
        print('\n\nWritten to json file.')
    return 

def real_world_scores(methods):
    """ I think unused function. 
    """

    with open('results/real_world_dataset_params.json', 'r') as fp:
        real_world_dataset_params = json.load(fp)   

    with open('results/best_models.json', 'r') as fp:
        best_models_params = json.load(fp)

    results = {}

    for method in methods:
        
        with open(f'results/real_world_results/{method}_real_world.json') as fp:
            method_results = json.load(fp)[method]

        runtimes = [] 
        for dataset, outcome in method_results.items():
            runtime = outcome['runtime']
            if runtime == None:
                print(f'{method} has no runtime value for {dataset}')
                continue

            runtimes.append(runtime)
        
        runtimes = np.asarray(runtimes)

        
methods = ['low_variance', 'lap_score', 'SPEC', 'MCFS', 'UDFS', 'NDFS']

# model_evaluation(methods)

best_models(methods)

# real_world_scores(methods)

