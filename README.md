# UFS
This repository contains the code associated to the paper "Runtime Prediction of Filter Unsupervised Feature Selection Methods" by Teun van der Weij, Venustiano Soancatl Aguilar, and Saúl Solorio-Fernández. 

Feel free to reach out to mailvanteun@gmail.com if you have any problems running the code, or if you have a question about the repository or paper as a whole. Lastly, if you have any suggestion to improve this repository, create an issue or send an email if that's more applicable. 

## Explanation
The structure of this repository is as follows. 
* src/experiment_files: This folder contains the code of the experiment. It does not contain the datasets as they are too large, you must create them yourself by using the make_datasets.py script. Each script contains information about how and when it should be used, starting by investigating experiment_config.py is advised. 
* changed_files: this folder contains files that are needed to update the scikit-feature repository [https://github.com/jundongl/scikit-feature] to newer versions of python and related packages. See the Installation note further down. 
* The other files contain other files which should be self-explanatory. However, it is noteworthy to state that we provide a dockerfile from which you can create an image and then execute the experiment in the container. Instructions are given below. 


## The docker container

[Get Docker](https://docs.docker.com/get-docker/) for Windows, Linux or OSX.

Under Windows Home I would recommend first to install [Windows
Subsystem for Linux
2](https://docs.microsoft.com/en-us/windows/wsl/install-win10) (WSL2)
and then to install Docker as in a [Linux
installation](https://docs.docker.com/engine/install/). For example,
[Ubuntu](https://docs.docker.com/engine/install/ubuntu/).

You could also [Install Docker Desktop on Windows
Home](https://docs.docker.com/docker-for-windows/install-windows-home/)
but you will need WSL2 anyway.

Installing docker on a VM it is also possible but it will be slower
than the above options.

## Docker

This docker file uses the 'datascience_notebook' from [Docker
Stacks](https://github.com/jupyter/docker-stacks).

## Installation note

To run the experiment, change the files in scikit-feature as stated in the changed_files folder.
In that folder you also find the directories of the files so it's easier to implement. 
Note: it is easiest to first change the files and then install scikit-feature through the 
command 'python setup.py install'. 

After that, download the datasets available in the google drive and put them in the data folder. 
The data folder can be found in the experiment_files folder. 

The requirements are available in the requirements.txt file. 
A docker container can be made with these requirements. 

### Building the image

Move to the root directory of the repository (where the 'Dockerfile'
is) and run


```
docker build --rm -t jupyter/ufs .
```

### Running the container

```
docker run --name UFS --rm -p 8888:8888 -v "$PWD"/src/:/home/jovyan/work  jupyter/ufs
```

- `--name UFS` name of the container. You can give any name you like.  
- `--rm` removes the container when the service is shutdown.  
- `-p 8888:8888` exposed ports.  
- `-v "$PWD"/UFS/:/home/jovyan/work/` mounts a volume containing the
  development code into
  `/home/jovyan/work/` folder in the container.  
- `UFS` name of the container.




