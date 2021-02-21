FROM jupyter/datascience-notebook:r-4.0.3

COPY --chown=${NB_UID}:${NB_GID} requirements.txt /tmp/

RUN conda install -c conda-forge --yes --file /tmp/requirements.txt && \
    fix-permissions $CONDA_DIR && \
    fix-permissions /home/$NB_USER

# Create a Python 2.x environment using conda including at least the ipython kernel
# and the kernda utility. Add any additional packages you want available for use
# in a Python 2 notebook to the first line here (e.g., pandas, matplotlib, etc.)

RUN conda create --quiet --yes -p $CONDA_DIR/envs/python2 python=2.7 ipython ipykernel kernda numpy scikit-learn==0.20.4 && \
	conda clean --all -f -y

# Install scikit-feature which does not have a pip or conda package at the moment
WORKDIR /tmp
COPY ./changed_files/* /tmp/changed_files/
RUN ls -l /tmp/changed_files/NDFS.py && \
	whoami &&\
	git clone https://github.com/jundongl/scikit-feature.git && \
	cd ./scikit-feature/ && \
	cp ../changed_files/NDFS.py ./skfeature/function/sparse_learning_based/ && \
	cp ../changed_files/unsupervised_evaluation.py ./skfeature/utility/ && \
	cp ../changed_files/test* ./skfeature/example/ && \
	python setup.py install

# https://pythonspeed.com/articles/activate-conda-dockerfile/
# Make RUN commands use the new environment:
SHELL ["conda", "run", "-n", "python2", "/bin/bash", "-c"]

RUN cd ./scikit-feature/ && \
	python setup.py install && \
	rm -rf /tmp/scikit-feature && \
	fix-permissions "${CONDA_DIR}" && \
	fix-permissions "/home/${NB_USER}"


USER root

# Create a global kernelspec in the image and modify it so that it properly activates
# the python2 conda environment.
RUN $CONDA_DIR/envs/python2/bin/python -m ipykernel install && \
$CONDA_DIR/envs/python2/bin/kernda -o -y /usr/local/share/jupyter/kernels/python2/kernel.json

USER $NB_USER
WORKDIR $HOME
