## How to run locally

```bash
# Pull the database image and run the image inside a container
docker run \
--name web-chat-database \
-p 5432:5432 \
-e POSTGRES_PASSWORD=mypassword \
-e POSTGRES_USER=myusername \
-e POSTGRES_DB=web-chat-postgres \
-d postgres

# Build the message broker image
docker build -t web-chat-message-broker -f MessageBrokerDockerfile a

# Run the message broker image inside a container

docker run  \
-hostname my-rabbit \
--name web-chat-message-broker \
-p 5672:5672 \
-p 61613:61613 \
-p 15672:15672 \
-e RABBITMQ_DEFAULT_USER=myusername1 \
-e RABBITMQ_DEFAULT_PASS=mypassword1 \
-e STOMP_USER=myusername2
-e STOMP_PASSWORD=mypassword2
-d web-chat-message-broker

# Build the server image 
docker build -t web-chat-api-server .

# Run the server image inside a container
docker run \
--name web-chat-api-server \
-p 8080:8080 \
-e DATABASE_URL="jdbc:postgresql://172.17.0.1:5432/web-chat-postgres" \
-e DATABASE_USERNAME=myusername \\ 
-e DATABASE_PASSWORD=mypassword web-chat-api-server
```

## How to run tests locally

Run integration tests with a H2 in-memory database
```bash
# Build the server image 
docker build -t web-chat-api-server --target=build_stage api-server

# Run the server image inside a container
docker run --name web-chat-api-server web-chat-api-server ./mvnw test ./mvnw test -Dspring.profiles.active=test-with-H2
```

Run integration tests with a Postgres database
```bash



# Pull the database image and run the image inside a container
docker run \
--name web-chat-database \
-p 5432:5432 \
-e POSTGRES_PASSWORD=mypassword \
-e POSTGRES_USER=myusername \
-e POSTGRES_DB=web-chat-postgres \
-d postgres

# Build the message broker image
docker build -t web-chat-message-broker message-broker

# Run the message broker image inside a container
docker run  \
--name web-chat-message-broker \
--hostname my-rabbit \
-p 5672:5672 \
-p 61613:61613 \
-p 15672:15672 \
-e RABBITMQ_DEFAULT_USER=myusername1 \
-e RABBITMQ_DEFAULT_PASS=mypassword1 \
-e STOMP_USERNAME=myusername2 \
-e STOMP_PASSWORD=mypassword2 \
-d web-chat-message-broker

# Build the server image 
docker build -t web-chat-api-server --target=build_stage api-server

# Run the server image inside a container
docker run \
--name web-chat-api-server \
-p 8080:8080 \
-e DATABASE_URL="jdbc:postgresql://172.17.0.1:5432/web-chat-postgres" \
-e DATABASE_USERNAME=myusername \
-e DATABASE_PASSWORD=mypassword \
-e MESSAGE_BROKER_URL="172.17.0.1" \
-e MESSAGE_BROKER_PORT=61613 \
-e MESSAGE_BROKER_USERNAME=myusername2 \
-e MESSAGE_BROKER_PASSWORD=mypassword2 \
web-chat-api-server \
./mvnw test -Dspring.profiles.active=test-with-Postgres-database
```

