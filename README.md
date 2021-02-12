# UFS

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

### Working with the container

#### Using jupyter notebook

After running the container a link is displayed to access the notebook.

```
...
http://127.0.0.1:8888/?token=4bd2d4d3f16958b4c084442941bf00e902b40ffd6ecc40a8
```

#### Useful commands

- List docker images

``` bash
docker images
```

- running containers

```
docker ps
```
- 
#### Executing commands in the container command line

```bash
docker exec -it UFS bash  
```
##Teun's branch.

To run the experiment, change the files in scikit-feature as stated in the changed_files folder.
In that folder you also find the directories of the files so it's easier to implement. 
Note: it is easiest to first change the files and then install scikit-feature through the 
command 'python setup.py install'. 

After that, download the datasets available in the google drive and put them in the data folder. 
The data folder can be found in the experiment_files folder. 

The requirements are available in the requirements.txt file. 
A docker container can be made with these requirements. 
