
""" This file makes the plots 

NOTE: this cannot be run with matplotlib version < 3.4
    In this case it cannnot be run in the container. 
"""
import pandas as pd
import matplotlib.pyplot as plt
import json 
import numpy as np
import seaborn as sns

from sklearn.metrics import mean_absolute_error, mean_squared_error

# sets a nice plotting theme for all the plot in the script
sns.set_style('whitegrid') 

# have fontdict for titles as global variable so they are the same for every plot 
fontdict = {'family': 'serif',
        'color':  'k',
        'weight': 'normal',
        'size': 22}


def power_model(X, b1, b2, b3):
    """
    The power model, as defined in the paper. 
    """
    x1, x2 = X
    return b1 * np.power(x1, b2) * np.power(x2, b3)


def exponential_linear(X, b1, b2, b3):
    """
    The exponential model, as defined in the paper. 
    """
    x1, x2 = X
    return b1 * np.exp(b2 * x1) * (b3 * x2)
    
def plot_synthetic(methods, X_axis):
    
    """
    Function that plots the synthetic runtime data. 
    
    PARAMS:
    X_axis: what you want the x-axis to represent. 
        The options are features, objects and objects times features.
        The latter you write as objects_x_features 

    methods: the method you want plotted. It automatically grabs the
        right dataset. 

    ------------
    Returns:

    Nothing, but saves a pdf of the plot in the specified directory.

    NOTE: paper format had to be black and white, 
        commented out is the original colour formatting. 
    """

    fig, axs = plt.subplots(3, 2, sharex=True, figsize=(8, 10))

    # get the parameters of the best models to show them on the synthetic plot
    with open('results/best_models.json', 'r') as fp:
        best_models_params = json.load(fp)

    # get the dataset parameters of the synthetic datasets 
    with open('results/synthetic_dataset_params.json', 'r') as fp:
        synthetic_dataset_params = json.load(fp)
    
    objects = [] 
    features = []
    for dataset in synthetic_dataset_params.keys():
        objects.append(synthetic_dataset_params[dataset]['n_objects'])
        features.append(synthetic_dataset_params[dataset]['n_features'])

    objects = np.asarray(objects)
    features = np.asarray(features)

    for ax, method in zip(axs.flat, methods):
        # print method to indicate progress
        print(method)
        
        # get the right title for  each subplot
        if method == 'low_variance':
            ax.set_title('Low Variance')
        elif method == 'lap_score':
            ax.set_title('Laplacian Score')
        else:
            ax.set_title(method)
        
        src = f'results/synthetic_results/{method}_synthetic.json'

        with open(src) as fp:
            synthetic_runtimes = json.load(fp)

        true_runtimes = []  

        # get the true runtimes for each method
        for dataset in synthetic_dataset_params.keys():
            true_runtimes.append(synthetic_runtimes[method][dataset]['runtime'])

        true_runtimes = np.asarray(true_runtimes)
        
        if X_axis == 'objects':
            features_sizes = [0.5 * x / 100 for x in features]
            ax.scatter(objects, true_runtimes, s=features_sizes, label='True runtimes', edgecolors='black', facecolors='white', alpha=0.7)
            
            # add the predictions of the best model
            if method in ['lap_score', 'SPEC', 'MCFS']:
                X = (objects, features)
                p_opt = best_models_params[method]['power_model']['betas']
                ax.scatter(objects, power_model(X, *p_opt), s=features_sizes, c='black', marker='s', label='Power model prediction', alpha=0.7)
                
                # get legend for one plot
                if method == 'lap_score':
                    ax.legend()   

        if X_axis == 'features':
            objects_sizes = [0.5 * x / 100 for x in objects]
            # ax.scatter(features, true_runtimes, c='black', alpha=0.7, s=objects_sizes, label='True runtimes')
            ax.scatter(objects, true_runtimes, s=objects_sizes, label='True runtimes', 
                        edgecolors='black', facecolors='white', alpha=0.7)

            # add the predictions of the best model
            if method in ['UDFS', 'NDFS']:
                X = (objects, features)
                
                p_opt = best_models_params[method]['power_model']['betas']
                # ax.scatter(features, power_model(X, *p_opt), s=objects_sizes, alpha=0.7, c='#404040', marker='s', label='Power model prediction')
                ax.scatter(objects, power_model(X, *p_opt), s=objects_sizes, c='black', 
                            marker='s', label='Power model prediction', alpha=0.7)

                X = (features, objects)
                p_opt = best_models_params[method]['exponential_linear_features']['betas']
                ax.scatter(features, exponential_linear(X, *p_opt), s=objects_sizes, alpha=0.7, 
                            marker='^', c='grey', label='EL prediction')
                
                if method == 'UDFS':
                    ax.legend()              

        elif X_axis == 'objects_x_features':

            objects_x_features = objects * features 
            ax.scatter(objects_x_features, true_runtimes, edgecolors='black', 
                    facecolors='white', alpha=0.7, s=10, label='True runtimes')

            if method == 'low_variance':

                slope = best_models_params['low_variance']['slope']
                ax.scatter(objects_x_features, slope * objects_x_features,
                            alpha=0.7, c='grey', s=5, marker='v', label='Simple linear Regression prediction')

                X = (objects, features)
                p_opt = best_models_params[method]['power_model']['betas']
                # ax.scatter(objects_x_features, power_model(X, *p_opt), s=5, alpha=0.7, c='grey', marker='^', label='Power prediction')
                ax.scatter(objects_x_features, power_model(X, *p_opt), s=5, c='black', marker='s', 
                            label='Power model prediction', alpha=0.7)

                ax.legend()         

    if X_axis == 'objects_x_features':
        fig.supxlabel('Data points')
    else:
        fig.supxlabel(X_axis[0].upper() + X_axis[1:])

    fig.supylabel('Runtime in seconds')
    fig.tight_layout()
    
    fig.savefig(f"plots/greyscale_synthetic_{X_axis}.pdf", bbox_inches='tight') 
    # plt.show()

def plot_real_world(methods):
    
    """
    Function that plots the real-world runtime data. 
    
    PARAMS:
    X_axis: what you want the x-axis to represent. 
        The options are features, objects and objects times features.
        The latter you write as objects_x_features 

    methods: the method you want plotted. It automatically grabs the
        right dataset. 

    ------------
    Returns:

    Nothing, but saves a pdf of the plot in the specified directory.
    """

    with open('results/real_world_dataset_params.json', 'r') as fp:
        real_world_dataset_params = json.load(fp)    

    with open('results/best_models.json', 'r') as fp:
        best_models_params = json.load(fp)

    best_model_eval = {'low_variance':{}, 'lap_score':{}, 
                    'SPEC':{}, 'MCFS':{}, 'UDFS':{}, 'NDFS':{}} 

    fig, axs = plt.subplots(3, 2, sharex=False, sharey=False, figsize=(8, 11))

    methods_part1 = ['lap_score', 'SPEC', 'MCFS']
    X_axis = 'objects'
    for ax, method in zip(axs.flat, methods):

        src_real_world = f'results/real_world_results/{method}_real_world.json'
        with open(src_real_world) as fp:
            real_world_algo_data = json.load(fp)

        objects = [] 
        features = []
        true_runtimes = []  

        # get list of objects, features and runtimes specific to each method
        # filter out some datasets so the plotting is better
        for dataset, params in real_world_dataset_params.items():
            # the if statements allows filtering, see commented out lines
            # if error scores are calculated, don't comment out! (yes, not the best idea, but it's easier)
            if method in ['low_variance', 'lap_score', 'SPEC', 'MCFS']:
                if dataset == 'gisette.mat':
                    continue

                # so filter out USPS dataset for lap_score, SPEC and MCFS
                if method != 'low_variance':
                    if dataset ==  'USPS.mat':
                        continue

            if method in ['UDFS', 'NDFS']:
                if dataset == 'SMK-CAN-187.mat' or dataset == 'GLI-85.mat':
                    continue

            runtime = real_world_algo_data[method][dataset]['runtime']

            # check for when some results have not finished
            if runtime == None:
                print(f"{method} has no results for {dataset}")
                continue 

            true_runtimes.append(runtime)
            objects.append(params['objects'])
            features.append(params['features'])

        true_runtimes = np.asarray(true_runtimes)
        objects = np.asarray(objects)
        features = np.asarray(features)

        objects_sizes = [1 * x / 100 for x in objects]
        features_sizes = [0.5 * x / 100 for x in features]

        print("true runtimes length: ", method, len(true_runtimes))
        # to get average runtimes, don't filter out datasets when this is used
        # print("mean runtimes ", method, np.mean(true_runtimes))

        if method == 'low_variance':
            ax.set_title('Low Variance')

            objects_x_features = objects * features
            ax.scatter(objects_x_features, true_runtimes, s=5, label='True runtimes', edgecolors='black', facecolors='white', alpha=0.7)
            
            X = (objects, features)
            p_opt = best_models_params['low_variance']['power_model']['betas']
            
            predicted_runtimes_power = power_model(X, *p_opt)
            ax.scatter(objects_x_features, predicted_runtimes_power, s=5,
                         c='black', marker='s', label='Power model prediction', alpha=0.7)

            slope = best_models_params['low_variance']['slope']
            predicted_runtimes_lingress = slope*objects_x_features
            ax.scatter(objects_x_features, predicted_runtimes_lingress,
                            alpha=0.7, c='grey', s=5, marker='v', label='Simple linear Regression prediction')
            ax.set_xlabel('Data points')
            ax.legend()

            # save error scores to put in table
            best_model_eval[method].update({'lingress':
                    {'MAE':mean_absolute_error(true_runtimes, predicted_runtimes_lingress), 
                    'RMSE': mean_squared_error(true_runtimes, predicted_runtimes_lingress)}})

            best_model_eval[method].update({'power':
                    {'MAE':mean_absolute_error(true_runtimes, predicted_runtimes_power), 
                    'RMSE': mean_squared_error(true_runtimes, predicted_runtimes_power)}})

        elif method == 'lap_score':
            ax.set_title('Laplacian Score')

            ax.scatter(objects, true_runtimes, s=features_sizes, label='True runtimes', edgecolors='black', facecolors='white', alpha=0.7)

            X = (objects, features)
            p_opt = best_models_params['lap_score']['power_model']['betas']
            predicted_runtimes_power = power_model(X, *p_opt)

            ax.scatter(objects, predicted_runtimes_power, s=features_sizes, 
                        c='black', marker='s', label='Power model prediction', alpha=0.7)

            ax.set_xlabel('Objects')

            # save error scores to put in table
            best_model_eval[method].update({'power':
                    {'MAE':mean_absolute_error(true_runtimes, predicted_runtimes_power), 
                    'RMSE': mean_squared_error(true_runtimes, predicted_runtimes_power)}})

        elif method == 'SPEC':
            ax.set_title('SPEC')

            ax.scatter(objects, true_runtimes, s=features_sizes, label='True runtimes', edgecolors='black', facecolors='white', alpha=0.7)

            X = (objects, features)
            p_opt = best_models_params['SPEC']['power_model']['betas']
            predicted_runtimes_power = power_model(X, *p_opt)

            ax.scatter(objects, predicted_runtimes_power, s=features_sizes, 
                        c='black', marker='s', label='Power model prediction', alpha=0.7)

            ax.set_xlabel('Objects')

            # save error scores to put in table
            best_model_eval[method].update({'power':
                    {'MAE':mean_absolute_error(true_runtimes, predicted_runtimes_power), 
                    'RMSE': mean_squared_error(true_runtimes, predicted_runtimes_power)}})

        elif method == 'MCFS':
            ax.set_title('MCFS')

            ax.scatter(objects, true_runtimes, s=features_sizes, label='True runtimes', edgecolors='black', facecolors='white', alpha=0.7)
            
            X = (objects, features)
            p_opt = best_models_params['MCFS']['power_model']['betas']
            predicted_runtimes_power = power_model(X, *p_opt)

            ax.scatter(objects, predicted_runtimes_power, s=features_sizes, 
                        c='black', marker='s', label='Power model prediction', alpha=0.7)

            ax.set_xlabel('Objects')

            # save error scores to put in table
            best_model_eval[method].update({'power':
                    {'MAE':mean_absolute_error(true_runtimes, predicted_runtimes_power), 
                    'RMSE': mean_squared_error(true_runtimes, predicted_runtimes_power)}})
                
        elif method == 'UDFS':
            ax.set_title('UDFS')

            ax.scatter(features, true_runtimes, s=features_sizes, label='True runtimes', edgecolors='black', facecolors='white', alpha=0.7)
            
            X = (objects, features)
            p_opt = best_models_params['UDFS']['power_model']['betas']
            predicted_runtimes_power = power_model(X, *p_opt)
            ax.scatter(features, predicted_runtimes_power, s=objects_sizes, 
                        c='black', marker='s', label='Power model prediction', alpha=0.7)

            X = (features, objects)
            p_opt = best_models_params[method]['exponential_linear_features']['betas']
            predicted_runtimes_EL = exponential_linear(X, *p_opt)
            
            ax.scatter(features, predicted_runtimes_EL, s=objects_sizes, alpha=0.7, 
                            marker='^', c='grey', label='EL prediction')

            ax.legend()
            ax.set_xlabel('Features')

            # save error scores to put in table
            best_model_eval[method].update({'power':
                    {'MAE':mean_absolute_error(true_runtimes, predicted_runtimes_power), 
                    'RMSE': mean_squared_error(true_runtimes, predicted_runtimes_power)}})

            best_model_eval[method].update({'EL_features':
                    {'MAE':mean_absolute_error(true_runtimes, predicted_runtimes_EL), 
                    'RMSE': mean_squared_error(true_runtimes, predicted_runtimes_EL)}})

        elif method == 'NDFS':
            ax.set_title('NDFS')
            ax.scatter(features, true_runtimes, s=features_sizes, label='True runtimes', edgecolors='black', facecolors='white', alpha=0.7)
            
            X = (objects, features)
            p_opt = best_models_params['NDFS']['power_model']['betas']
            predicted_runtimes_power = power_model(X, *p_opt)
            ax.scatter(features, predicted_runtimes_power, s=objects_sizes, 
                        c='black', marker='s', label='Power model prediction', alpha=0.7)

            X = (features, objects)
            p_opt = best_models_params[method]['exponential_linear_features']['betas']
            predicted_runtimes_EL = exponential_linear(X, *p_opt)

            ax.scatter(features, predicted_runtimes_EL, s=objects_sizes, alpha=0.7, 
                            marker='^', c='grey')
                            
            ax.set_xlabel('Features')

            # save error scores to put in table
            best_model_eval[method].update({'power':
                    {'MAE':mean_absolute_error(true_runtimes, predicted_runtimes_power), 
                    'RMSE': mean_squared_error(true_runtimes, predicted_runtimes_power)}})
            
            best_model_eval[method].update({'EL_features':
                    {'MAE':mean_absolute_error(true_runtimes, predicted_runtimes_EL), 
                    'RMSE': mean_squared_error(true_runtimes, predicted_runtimes_EL)}})

    fig.supylabel('Runtime in seconds')
    # fig.legend()
    fig.tight_layout()

    # suggestion: plt.savefig(file.jpeg, edgecolor='black', dpi=400, facecolor='black', transparent=True) 
    fig.savefig(f"plots/greyscale_real_world_plot.pdf", bbox_inches='tight') 
    # plt.show() #the pdf does not have overlapping text with bbx_inches = 'tight', so saved pdf is better than plt.show()

    # uncomment this is if you want to save the real world scores
    # filename = 'results/real_world_scores.json'

    # with open(filename, 'w') as fp:
    #     json.dump(best_model_eval, fp, indent=4)

methods = ['low_variance', 'lap_score', 'SPEC', 'MCFS', 'UDFS', 'NDFS']

## Commented out so it does not run automatically. 
# plot_synthetic(methods, 'objects')
# plot_synthetic(methods, 'features')
# plot_synthetic(methods, 'objects_x_features')

# plot_real_world(methods)

