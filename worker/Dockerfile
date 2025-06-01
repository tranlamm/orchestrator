FROM python:3.12-slim

LABEL maintainer="tranlam021102eee@gmail.com"
LABEL version="1.0"
LABEL description="This is a python worker image"

ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

WORKDIR /app

COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

COPY config_docker.ini ./
COPY train_docker.py ./
COPY worker_docker.py ./

CMD ["python", "worker_docker.py"]