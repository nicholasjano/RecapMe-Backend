#!/bin/bash

# Load environment variables from .env file
source .env

# Run Docker container with environment variables
docker run -p 8080:8080 \
  -e GEMINI_API_KEY="$GEMINI_API_KEY" \
  -e DEVELOPMENT="$DEVELOPMENT" \
  -e API_KEY="$API_KEY" \
  -e REDIS_URL="$REDIS_URL" \
  --name recapme-backend \
  recapme-backend