FROM jupyter/base-notebook:x86_64-ubuntu-22.04

USER root

RUN mkdir -p /in
RUN mkdir -p /data
RUN mkdir -p /out

ADD ./src /app
ADD ./prepare_data.py /app
ADD ./Plot_Precision_Recall.ipynb /app
ADD ./requirements.txt /app
ADD ./pyproject.toml /app
ADD ./entrypoint.sh /app

WORKDIR /app

RUN python -m venv .venv
RUN pip install --editable .

ENTRYPOINT ["/usr/bin/bash", "/app/entrypoint.sh"]