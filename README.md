## How to run locally

```bash
# Pull the database image and run the image inside a container
docker run --name web-chat-database -p 5432:5432 -e POSTGRES_PASSWORD=mypassword -e POSTGRES_USER=myusername -e POSTGRES_DB=web-chat-postgres -d postgres

# Build the server image 
docker build -t web-chat-server .
# Run the server image inside a container
docker run --name web-chat-server -p 8080:8080 -e DATABASE_URL="jdbc:postgresql://172.17.0.1:5432/web-chat-postgres" -e DATABASE_USERNAME=myusername -e DATABASE_PASSWORD=mypassword web-chat-server
```

## How to run tests locally

Run integration tests with a H2 in-memory database
```bash
docker build -t web-chat-server --target=build_stage .
docker run web-chat-server ./mvnw test 
```

