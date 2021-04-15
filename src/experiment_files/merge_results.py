""" File that merges datasets where some results could not be 
    generated with other parameters such as runtime per combinations 
"""
import json
algorithm = 'UDFS'

with open(f'/home/jovyan/work/experiment_files/results/{algorithm}_long_combinations.json', 'r') as f:
    to_add = json.load(f)

with open(f'/home/jovyan/work/experiment_files/results/{algorithm}_peregrine_short_runtimes.json', 'r') as f:
    data = json.load(f)


# add every newly analyzed dataset to the data
for key in to_add[algorithm].keys():
    if data[algorithm][key]['nmi'] == None:
        data[algorithm][key] = to_add[algorithm][key]       
        
with open(f'results/{algorithm}_peregrine.json', 'w') as f:
    json.dump(data, f, indent=4)


# else:
#     # add parameters to later save to json file
#     results['num_features'] = num_features
#     results['W_kwargs'] = W_kwargs
#     results['max_run_time'] = max_run_time
#     results['max_global_time'] = max_global_time

#     with open(filename, 'w') as f:
#         json.dump(results, f, indent=4)

print('\n\nWritten to json file.')