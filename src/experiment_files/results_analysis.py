
""" This file makes the plots 

NOTE: this cannot be run with matplotlib version < 3.4
    In this case it cannnot be run in the container. 
"""
import pandas as pd
import matplotlib.pyplot as plt
import json 
import numpy as np
import seaborn as sns

# sets a nice plotting theme for all the plot in the script
sns.set_style('whitegrid') 

# have fontdict for titles as global variable so they are the same for every plot 
fontdict = {'family': 'serif',
        'color':  'k',
        'weight': 'normal',
        'size': 22}


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
    
def plot_synthetic(methods, X_axis):
    
    """
    Function that does unsupervised feature selection with laplacian score.

    PARAMS:
    X_axis: what you want the x-axis to represent. 
        The options are features, objects and objects times features.
        The latter you write as objects_x_features 

    method: the method you want analyzed. It automatically grabs the
        right dataset. 

    compare: to potentially compare another file with results
        which were received with running it again in peregrine. 

    ------------
    Returns:

    Nothing, but saves a pdf of the plot in the plots directory.
    """
    fig, axs = plt.subplots(3, 2, sharex=True, figsize=(8, 10))

    with open('results/best_models.json', 'r') as fp:
        best_models_params = json.load(fp)

    with open('results/synthetic_dataset_params.json', 'r') as fp:
        synthetic_dataset_params = json.load(fp)

    for ax, method in zip(axs.flat, methods):
        print(method)
        
        if method == 'low_variance':
            ax.set_title('Low Variance')
        elif method == 'lap_score':
            ax.set_title('Laplacian Score')
        else:
            ax.set_title(method)
        
        src = f'results/synthetic_results/{method}_synthetic.json'

        with open(src) as fp:
            synthetic_runtimes = json.load(fp)

        objects = [] 
        features = []
        true_runtimes = []  

        # slow to do it here but is temporary fix for UDFS not having completed all analayses
        for dataset, params in synthetic_runtimes[method].items():

            true_runtimes.append(synthetic_runtimes[method][dataset]['runtime'])
            objects.append(synthetic_dataset_params[dataset]['n_objects'])
            features.append(synthetic_dataset_params[dataset]['n_features'])

        objects = np.asarray(objects)
        features = np.asarray(features)
        true_runtimes = np.asarray(true_runtimes)
        
        # print("true runtimes, objects, features length: ", len(true_runtimes), len(objects), len(features))

        if X_axis == 'objects':
            features_sizes = [0.5 * x / 100 for x in features]
            ax.scatter(objects, true_runtimes, s=features_sizes, label='True runtimes')
            # ax.scatter(objects, true_runtimes, s=10, label='True runtimes')
            
            if method in ['lap_score', 'SPEC', 'MCFS']:
                X = (objects, features)
                p_opt = best_models_params[method]['power_model']['betas']
                # ax.scatter(objects, exponential_linear(X, *p_opt), s=features_sizes, c='orange', label='exponential_linear_objects prediction')
                ax.scatter(objects, power_model(X, *p_opt), s=10, c='orange', marker='*', label='Power model prediction')
                if method == 'lap_score':
                    ax.legend()   

        if X_axis == 'features':
            objects_sizes = [0.5 * x / 100 for x in objects]
            ax.scatter(features, true_runtimes, s=objects_sizes, label='True runtimes')
            # ax.scatter(features, true_runtimes, s=10, label='True runtimes')
            
            if method in ['UDFS', 'NDFS']:
                X = (objects, features)
                
                p_opt = best_models_params[method]['power_model']['betas']
                ax.scatter(features, power_model(X, *p_opt), s=objects_sizes, c='orange', marker='*', label='Power model prediction')

            if method == 'NDFS':
                X = (features, objects)
                p_opt = best_models_params[method]['exponential_linear_features']['betas']
                # ax.scatter(features, exponential_linear(X, *p_opt), s=10, c='orange', label='EL prediction')
                ax.scatter(features, exponential_linear(X, *p_opt), s=objects_sizes, c='red', marker='s', label='EL prediction')
                ax.legend()              

        elif X_axis == 'objects_x_features':

            objects_x_features = objects * features 
            ax.scatter(objects_x_features, true_runtimes, s=5, label='Runtimes')

            if method == 'low_variance':

                slope = best_models_params['low_variance']['slope']
                ax.scatter(objects_x_features, slope * objects_x_features,
                            c='orange', s=5, marker='X', label='Simple linear egression prediction')

                X = (objects, features)
                p_opt = best_models_params[method]['power_model']['betas']
                ax.scatter(objects_x_features, power_model(X, *p_opt), s=5, c='red', label='Power prediction')
                ax.legend()         


    if X_axis == 'objects_x_features':
        fig.supxlabel('Datapoints')
    else:
        fig.supxlabel(X_axis[0].upper() + X_axis[1:])

    fig.supylabel('Runtime in seconds')
    fig.tight_layout()

    # fig.savefig(f"good_plots/synthetic_and_best_predictions_{X_axis}.pdf", bbox_inches='tight') 
    plt.show()

def real_world_plot(methods, X_axis):
    
    """
    Function that does unsupervised feature selection with laplacian score.

    PARAMS:
    X_axis: what you want the x-axis to represent. 
        The options are features, objects and objects times features.
        The latter you write as objects_x_features 

    method: the method you want analyzed. It automatically grabs the
        right dataset. 

    compare: to potentially compare another file with results
        which were received with running it again in peregrine. 

    ------------
    Returns:

    Nothing, but saves a pdf of the plot in the plots directory.
    """
    intercept = False
    fig, axs = plt.subplots(3, 2, sharex=True, figsize=(8, 10))

    with open('results/real_world_dataset_params.json', 'r') as fp:
        real_world_dataset_params = json.load(fp)    

    with open('results/best_models_with_power.json', 'r') as fp:
        best_models_params = json.load(fp)

    for ax, method in zip(axs.flat, methods):
        
        if method == 'low_variance':
            ax.set_title('Low Variance')
        elif method == 'lap_score':
            ax.set_title('Laplacian Score')
        else:
            ax.set_title(method)
        
        src_real_world = f'results/real_world_results/{method}_real_world.json'

        with open(src_real_world) as fp:
            real_world_algo_data = json.load(fp)

        objects = [] 
        features = []
        true_runtimes = []  

        # same order because list is the same
        # slow to do it here but is temp fix for UDFS not having completed all analayses

        for dataset, params in real_world_dataset_params.items():
            # the if statements allows filtering, see commetned out lines
            # if dataset == 'gisette.mat':
            #     continue
            
            # elif params['objects'] > 3000:
            #     continue

            # if params['features'] > 10000:
            #     continue

            try:
                true_runtimes.append(real_world_algo_data[method][dataset]['runtime'])
                objects.append(params['objects'])
                features.append(params['features'])

            except KeyError:
                print(f"{method} has no results for {dataset}")

        objects = np.asarray(objects)
        features = np.asarray(features)
        true_runtimes = np.asarray(true_runtimes)
        
        print("true runtimes length: ", len(true_runtimes))

        if X_axis == 'objects':
            features_sizes = [0.5 * x / 100 for x in features]
            # ax.scatter(objects, true_runtimes, s=features_sizes, label='True runtimes')
            ax.scatter(objects, true_runtimes, s=10, label='True runtimes')
            
            if method in ['low_variance', 'lap_score', 'SPEC', 'MCFS']:
                X = (objects, features)
                p_opt = best_models_params[method]['power_model']['betas']
                # ax.scatter(objects, exponential_linear(X, *p_opt), s=features_sizes, c='orange', label='exponential_linear_objects prediction')
                ax.scatter(objects, power_model(X, *p_opt), s=10, c='orange', label='Power function prediction')
                if method == 'low_variance':
                    ax.legend()   

        if X_axis == 'features':
            objects_sizes = [0.5 * x / 100 for x in objects]
            # ax.scatter(objects, true_runtimes, s=features_sizes, label='True runtimes')
            ax.scatter(features, true_runtimes, s=10, label='True runtimes')
            
            if method in ['UDFS', 'NDFS']:
                # X = (features, objects)
                # p_opt = best_models_params[method]['exponential_linear_features']['betas']
                # # ax.scatter(objects, power_model(X, *p_opt), s=features_sizes, c='orange', label='exponential_linear_features prediction')
                # ax.scatter(features, exponential_linear(X, *p_opt), s=10, c='orange', label='exponential_linear_features prediction')
                # # ax.legend()  

                # here it is (objects, features) because that's how parameters were defined is best models
                X = (objects, features)
                
                p_opt = best_models_params[method]['power_model']['betas']
                # ax.scatter(objects, exponential_linear(X, *p_opt), s=features_sizes, c='orange', label='exponential_linear prediction')
                ax.scatter(features, power_model(X, *p_opt), s=10, c='red', label='Power prediction')
                # ax.legend()                    

        elif X_axis == 'objects_x_features':
            # label won't get used if ax.legend() is not called so can leave it here
            objects_x_features = objects * features 
            ax.scatter(objects_x_features, true_runtimes, s=10, label='Runtimes')

            if method == 'low_variance':
                if intercept:
                    y_intercept = best_models_params['low_variance']['y_intercept']
                else: 
                    y_intercept = 0

                slope = best_models_params['low_variance']['slope']
                sorted_arr = np.sort(objects_x_features)
                ax.scatter(objects_x_features, y_intercept + slope * objects_x_features,
                            c='orange', s=10, label='Linear Regression predicted')

                X = (objects, features)
                p_opt = best_models_params[method]['power_model']['betas']
                # ax.scatter(objects, exponential_linear(X, *p_opt), s=features_sizes, c='orange', label='exponential_linear prediction')
                ax.scatter(objects_x_features, power_model(X, *p_opt), s=10, c='red', label='Power prediction')
                # ax.legend()         

        # if method == 'lap_score':
        #     handles, labels = ax.get_legend_handles_labels()
        #     fig.legend(handles, labels)#, loc='upper left')
        #     print(f'Handles: {handles} Labels: {labels}')

    # fig.suptitle('Run time results', fontdict=fontdict)
    if X_axis == 'objects_x_features':
        fig.supxlabel('Datapoints')
    else:
        fig.supxlabel(X_axis[0].upper() + X_axis[1:])
    fig.supylabel('Runtime in seconds')
    # fig.legend()
    # legend_without_duplicate_labels(ax)
    fig.tight_layout()
    # if intercept:
        # fig.savefig(f"good_plotssdsdsd/sdsdsreal_world_intercept{X_axis}.pdf", bbox_inches='tight')   
    # else:
    #     fig.savefig(f"good_plots/power_real_world_{X_axis}.pdf", bbox_inches='tight') 
    plt.show()

algos = ['low_variance', 'lap_score', 'SPEC', 'MCFS', 'UDFS', 'NDFS']
plot_synthetic(algos, 'objects')
plot_synthetic(algos, 'features')
plot_synthetic(algos, 'objects_x_features')