# Run locally
```bash
docker-compose build
docker-compose --env-file=.env --profile production up --detach
docker-compose --env-file=.env --profile production down
```

# Test locally

Run integration tests:

```bash
docker-compose build
docker-compose --env-file=.env --profile test up --abort-on-container-exit
docker-compose --env-file=.env --profile test down
```