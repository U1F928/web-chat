# Prerequisites

* Docker Compose V2

# Run locally
```bash
# Rebuild the images and start the containers
docker compose --env-file=.env.example --profile production up --build --detach 
# Stop and remove the containers
docker compose --env-file=.env.example --profile production down
```

# Test locally

Run integration tests:

```bash
# Rebuild the images and start the containers, stop the containers when the tests finish
docker compose --env-file=.env.example --profile test up --build --abort-on-container-exit 
# Remove the containers
docker compose --env-file=.env.example --profile test down
```