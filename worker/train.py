import argparse
import torch
import torchvision
import torch.nn as nn
import torch.nn.functional as F
import torch.optim as optim
from confluent_kafka import Producer
import configparser
import json
from enum import Enum
from sklearn.metrics import f1_score
from datetime import datetime
import time
import io
import gridfs
from pymongo import MongoClient

class OPERATION_MODE(Enum):
    TRAIN = 1
    VALIDATE = 2
    TEST = 3

def loadConfig():
    config = configparser.ConfigParser()
    config.read('config.ini')
    global host, port, topic_init, topic_training, topic_validation, topic_end, \
    producer, hostMongo, portMongo, usenameMongo, passwordMongo, databaseMongo, collectionMongo

    host = config['kafka']['host']
    port = int(config['kafka']['port'])
    topic_init = config['kafka']['topic_init']
    topic_training = config['kafka']['topic_training']
    topic_validation = config['kafka']['topic_validation']
    topic_end = config['kafka']['topic_end']
    conf = {
        'bootstrap.servers': f"{host}:{port}"
    }
    producer = Producer(conf)

    hostMongo = config['mongodb']['host']
    portMongo = int(config['mongodb']['port'])
    usenameMongo = config['mongodb']['username']
    passwordMongo = config['mongodb']['password']
    databaseMongo = config['mongodb']['database']
    collectionMongo = config['mongodb']['collection']

loadConfig()

def sendKafka(topic, value):
    def delivery_report(err, msg):
        if err is not None:
            print(f"Delivery failed: {err}")
        else:
            print(f"Message delivered to {msg.topic()} [{msg.partition()}]")

    producer.produce(topic, value=value, callback=delivery_report)
    producer.flush()

parser = argparse.ArgumentParser()
parser.add_argument('--modelId')
parser.add_argument('--epochs')
parser.add_argument('--batchSize')
parser.add_argument('--learningRate')
parser.add_argument('--logIntervals')
args = parser.parse_args()

model_id = args.modelId
n_epochs = int(args.epochs)
batch_size_train = int(args.batchSize)
learning_rate = float(args.learningRate)
log_interval = int(args.logIntervals)
batch_size_test = 1000

random_seed = 93
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
torch.manual_seed(random_seed)

full_train_data = torchvision.datasets.MNIST('./data_mnist/', train=True, download=True,
                                              transform=torchvision.transforms.Compose([
                                                torchvision.transforms.ToTensor(),
                                                torchvision.transforms.Normalize(
                                                  (0.1307,), (0.3081,))
                                              ]))

train_size = int(0.8 * len(full_train_data)) 
val_size = len(full_train_data) - train_size
train_dataset, val_dataset = torch.utils.data.random_split(full_train_data, [train_size, val_size])
train_loader = torch.utils.data.DataLoader(train_dataset, batch_size=batch_size_train, shuffle=True)
val_loader = torch.utils.data.DataLoader(val_dataset, batch_size=batch_size_test, shuffle=False)

test_loader = torch.utils.data.DataLoader(
  torchvision.datasets.MNIST('./data_mnist/', train=False, download=True,
                             transform=torchvision.transforms.Compose([
                               torchvision.transforms.ToTensor(),
                               torchvision.transforms.Normalize(
                                 (0.1307,), (0.3081,))
                             ])),
  batch_size=batch_size_test, shuffle=False)

class SimpleNet(nn.Module):
    def __init__(self):
        super(SimpleNet, self).__init__()
        self.net = nn.Sequential(
            nn.Conv2d(1, 32, kernel_size=3),
            nn.ReLU(),
            nn.MaxPool2d(2),                 
            nn.Conv2d(32, 64, kernel_size=3),
            nn.ReLU(),
            nn.MaxPool2d(2),                
            nn.Flatten(),
            nn.Linear(64 * 5 * 5, 128),
            nn.ReLU(),
            nn.Linear(128, 10)
        )
        
    def forward(self, x):
        return self.net(x)
    
network = SimpleNet().to(device)
criterion = nn.CrossEntropyLoss()
optimizer = optim.Adam(network.parameters(), lr=learning_rate)

def train(epoch):
  network.train()
  
  for batch_idx, (images, labels) in enumerate(train_loader):
    images, labels = images.to(device), labels.to(device)
    output = network(images)
    loss = criterion(output, labels)
    optimizer.zero_grad()
    loss.backward()
    optimizer.step()
    preds = torch.argmax(output, dim=1)
    correct = (preds == labels).sum().item()
    acc = correct / labels.size(0)

    if (batch_idx + 1) % log_interval == 0:
      sendModelTrainingInfo(
        epoch_idx=epoch,
        batch_idx=batch_idx + 1, 
        train_acc=round(acc, 2), 
        train_loss=round(loss.item(), 4),
      )
    
def evaluate(loader):
  network.eval()
  total_loss = 0
  correct = 0
  total = 0
  all_preds = []
  all_labels = []

  with torch.no_grad():
    for images, labels in loader:
      images, labels = images.to(device), labels.to(device)
      output = network(images)
      loss = criterion(output, labels)

      preds = torch.argmax(output, dim=1)
      correct += (preds == labels).sum().item()
      total += labels.size(0)
      total_loss += loss.item()
      all_preds.extend(preds.cpu().numpy())
      all_labels.extend(labels.cpu().numpy())

  avg_loss = total_loss / len(loader)
  avg_acc = correct / total
  overall_f1 = f1_score(all_labels, all_preds, average='macro')
  return avg_loss, avg_acc, overall_f1
      
def sendModelInitInfo(start_time):
  data = {
    "model_id": model_id,
    "n_epochs": n_epochs,
    "learning_rate": learning_rate,
    "batch_size": batch_size_train,
    "start_time": start_time,
    "log_interval": log_interval,
    "total_batch": len(train_loader)
  }
  sendKafka(topic_init, json.dumps(data))
  
def sendModelTrainingInfo(epoch_idx, batch_idx, train_acc, train_loss):
  data = {
    "model_id": model_id,
    "epoch_idx": epoch_idx,
    "batch_idx": batch_idx,
    "train_acc": train_acc,
    "train_loss": train_loss,
  }
  sendKafka(topic_training, json.dumps(data))
  
def sendModelValidationInfo(epoch_idx, val_acc, val_loss):
  data = {
    "model_id": model_id,
    "epoch_idx": epoch_idx,
    "val_acc": val_acc,
    "val_loss": val_loss
  }
  sendKafka(topic_validation, json.dumps(data))
  
def sendModelEndInfo(end_time, test_acc, test_loss, f1_score):
  data = {
    "model_id": model_id,
    "end_time": end_time,
    "test_acc": test_acc,
    "test_loss": test_loss,
    "f1_score": f1_score
  }
  sendKafka(topic_end, json.dumps(data))
  
def initTrain():
  timestamp_seconds = int(time.time()) * 1000
  sendModelInitInfo(timestamp_seconds)
  
def processTrain():
  for epoch in range(n_epochs):
    train(epoch=epoch+1)
    val_loss, val_acc, f1 = evaluate(val_loader)
    sendModelValidationInfo(epoch+1, val_acc, val_loss)
    
def endTrain():
  timestamp_seconds = int(time.time()) * 1000
  loss, acc, f1 = evaluate(test_loader)
  sendModelEndInfo(timestamp_seconds, acc, loss, f1)

def saveModel():
  # Serialize
  buffer = io.BytesIO()
  torch.save(network.state_dict(), buffer)
  buffer.seek(0)

  # Create the MongoDB URI with authentication
  uri = f"mongodb://{usenameMongo}:{passwordMongo}@{hostMongo}:{portMongo}/{databaseMongo}"

  # Connect to MongoDB
  client = MongoClient(uri)
  db = client[databaseMongo]
  fs = gridfs.GridFS(db, collection=collectionMongo)

  # Save to GridFS
  fs.put(buffer.getvalue(), filename=f"model_{model_id}_net.pt", contentType="application/octet-stream")
  
initTrain()
processTrain()
endTrain()
saveModel()