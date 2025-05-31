# Distributed Model Training System

This project is a distributed system for training machine learning models. It consists of two main components: `server` and `worker`. The system is designed to orchestrate model training tasks, manage training results, and provide real-time updates on the training status. Additionally, it supports dynamic scaling of `worker nodes` to efficiently handle increased training workloads and accelerate the training process.

## Project Structure

### `server` (Java Spring Boot)
The `server` acts as the orchestrator, responsible for:
- Dispatching training jobs to worker nodes via RabbitMQ.
- Organizing finished model training results and current training statuses.
- Caching current training results using Redis for faster queries.
- Persisting finished model results in MongoDB.
- Providing real-time updates to clients via WebSocket.
- Securing APIs using Spring Security and JWT for authentication and authorization.
- Documenting APIs using Swagger.

### `worker` (Python)
The `worker` is a containerized node responsible for:
- Training machine learning models using PyTorch (MNIST dataset).
- Sending training results back to the server via Kafka.
- Running inside Docker containers and orchestrated by Kubernetes.

## Tech Stack

- **Languages**: Java, Python
- **Frameworks**: Spring Boot, PyTorch
- **Messaging**: RabbitMQ, Kafka
- **Databases**: Redis (cache), MongoDB (persistent storage)
- **Containerization**: Docker
- **Orchestration**: Kubernetes
- **API Documentation**: Swagger
- **Security**: Spring Security, JWT
- **Real-time Communication**: WebSocket

## How It Works

1. **Server**:
    - Receives training requests from clients.
    - Dispatches jobs to worker nodes via RabbitMQ.
    - Caches intermediate training results in Redis.
    - Stores completed training results in MongoDB.
    - Provides real-time updates to clients using WebSocket.

2. **Worker**:
    - Listens for training jobs from RabbitMQ.
    - Trains the model using PyTorch (MNIST dataset).
    - Sends training results back to the server via Kafka.

## Prerequisites

- Java 17 or higher
- Python 3.8 or higher
- Docker
- Kubernetes
- RabbitMQ
- Kafka
- Redis
- MongoDB

## Setup Instructions

### Server
1. Navigate to the `server` folder.
2. Build the project using Maven:
    ```bash
   mvn clean install
3. Run the Spring Boot application:
    ```bash
   java -jar target/server-<version>.jar
   ```

### Worker
1. Navigate to the `worker` folder.
2. Build the Docker image:
    ```bash
   docker build -t worker-node .
3. Deploy the worker nodes using Kubernetes:
    ```bash
   kubectl apply -f 
   kubernetes/worker-deployment.yaml
   ```

## API Documentation
- The server provides Swagger-based API documentation. Once the server is running, access the Swagger UI at:
```
   http://<server-host>:<server-port>/swagger-ui.html
```

## Security
- Authentication and authorization are handled using Spring Security and JWT.
- Ensure valid JWT tokens are included in API requests for secure access.

## Real-time Updates
- Clients can subscribe to WebSocket endpoints to receive real-time updates on model training statuses.

## Contributors
#### Lam Tran
```
   github: https://github.com/tranlamm
   email: tranlam021102eee@gmail.com
```
