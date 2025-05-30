FROM python:3.12-slim

LABEL maintainer="tranlam20204759@gmail.com"
LABEL version="1.0"
LABEL description="This is a python worker image"

WORKDIR /app

COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

COPY train.py ./
COPY worker.py ./

RUN useraddr user
USER user

CMD ["python", "worker.py"]