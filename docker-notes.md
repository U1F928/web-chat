## Run locally

```bash
# Pull the database image and run the image inside a container
docker run \
--name web-chat-database \
-p 5432:5432 \
-e POSTGRES_PASSWORD=mypassword \
-e POSTGRES_USER=myusername \
-e POSTGRES_DB=web-chat
-d postgres

# Build the message broker image
docker build -t web-chat-message-broker message-broker

# Run the message broker image inside a container
docker run  \
--name web-chat-message-broker \
--hostname my-rabbit \
-p 61613:61613 \
-p 15672:15672 \
-e RABBITMQ_DEFAULT_USER=myusername1 \
-e RABBITMQ_DEFAULT_PASS=mypassword1 \
-d web-chat-message-broker

# Build the server image 
docker build -t web-chat-api chat-api

# Run the server image inside a container
docker run \
--name web-chat-api \
-p 8080:8080 \
-e DATABASE_URL="jdbc:postgresql://172.17.0.1:5432/web-chat" \
-e DATABASE_USERNAME=myusername \
-e DATABASE_PASSWORD=mypassword \
-e MESSAGE_BROKER_URL="172.17.0.1" \
-e MESSAGE_BROKER_PORT=61613 \
-e MESSAGE_BROKER_USERNAME=myusername1 \
-e MESSAGE_BROKER_PASSWORD=mypassword1 \
web-chat-api
```

## Test locally

Run integration tests with a Postgres database
```bash

# Pull the database image and run the image inside a container
docker run \
--name web-chat-database \
-p 5432:5432 \
-e POSTGRES_PASSWORD=mypassword \
-e POSTGRES_USER=myusername \
-d postgres

# Build the message broker image
docker build -t web-chat-message-broker message-broker

# Run the message broker image inside a container
docker run  \
--name web-chat-message-broker \
--hostname my-rabbit \
-p 61613:61613 \
-p 15672:15672 \
-e RABBITMQ_DEFAULT_USER=myusername1 \
-e RABBITMQ_DEFAULT_PASS=mypassword1 \
-d web-chat-message-broker

# Build the server image 
docker build -t web-chat-api --target=build_stage chat-api

# Run the server image inside a container
docker run \
--name web-chat-api \
-p 8080:8080 \
-e DATABASE_URL="jdbc:postgresql://172.17.0.1:5432/web-chat" \
-e DATABASE_USERNAME=myusername \
-e DATABASE_PASSWORD=mypassword \
-e MESSAGE_BROKER_URL="172.17.0.1" \
-e MESSAGE_BROKER_PORT=61613 \
-e MESSAGE_BROKER_USERNAME=myusername1 \
-e MESSAGE_BROKER_PASSWORD=mypassword1 \
web-chat-api \
./mvnw test -Dspring.profiles.active=test-with-Postgres-database
```