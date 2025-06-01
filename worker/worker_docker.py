import pika
import threading
import json
import configparser
import subprocess
from concurrent.futures import ThreadPoolExecutor

# Load config
def loadConfig():
    config = configparser.ConfigParser()
    config.read('config_docker.ini')
    global host, port, queue_name, username, password, executor

    host = config['rabbitmq']['host']
    port = int(config['rabbitmq']['port'])
    queue_name = config['rabbitmq']['queue']
    username = config['rabbitmq']['username']
    password = config['rabbitmq']['password']
    executor = ThreadPoolExecutor(max_workers=3)

def createConnection():
    global connection, channel
    credentials = pika.PlainCredentials(username=username, password=password)
    connection = pika.BlockingConnection(pika.ConnectionParameters(
        host=host,
        port=port,
        credentials=credentials
    ))
    channel = connection.channel()
    channel.queue_declare(queue=queue_name, durable=True)
    channel.basic_qos(prefetch_count=3)
    channel.basic_consume(queue=queue_name, on_message_callback=callback)
    print("LISTENING")
    channel.start_consuming()
    
    
def callback(ch, method, properties, body):
    try:
        message = body.decode()
        data = json.loads(message)
        executor.submit(process_job, data, ch, method.delivery_tag)
    except Exception as e:
        ch.basic_nack(delivery_tag=method.delivery_tag, requeue=True)
        

def process_job(parameter, ch, delivery_tag):
    try:
        args = ['python', 'train_docker.py']
        for key, value in parameter.items():
            args.append(f'--{key}')
            args.append(str(value))

        print(f"[THREAD] Running: {' '.join(args)}")
        subprocess.run(args, check=True)
        ch.basic_ack(delivery_tag=delivery_tag)
    except Exception as e:
        ch.basic_nack(delivery_tag=delivery_tag, requeue=True)
        
loadConfig()
createConnection()