#!/bin/bash

# Set defaults if not provided by environment
export GEMINI_MODEL=${GEMINI_MODEL:-gemini-2.5-flash}
export DEVELOPMENT=${DEVELOPMENT:-false}
export PORT=${PORT:-8080}

# Validate required environment variables
if [ -z "$GEMINI_API_KEY" ]; then
    echo "Error: GEMINI_API_KEY environment variable is required"
    exit 1
fi

# API_KEY is only required in production mode
if [ "$DEVELOPMENT" = "false" ] && [ -z "$API_KEY" ]; then
    echo "Error: API_KEY environment variable is required in production mode"
    exit 1
fi

# Set default API_KEY for development
if [ "$DEVELOPMENT" = "true" ] && [ -z "$API_KEY" ]; then
    export API_KEY="dev-api-key"
    echo "Development mode: Using default API key"
fi

echo "Starting RecapMe Backend on port $PORT..."
echo "Development mode: $DEVELOPMENT"
echo "Gemini model: $GEMINI_MODEL"

# Start the Spring Boot application with optimized JVM settings for Render
exec java \
    -Dserver.port=${PORT} \
    -Dserver.shutdown=immediate \
    -Dspring.lifecycle.timeout-per-shutdown-phase=10s \
    -Djava.awt.headless=true \
    -XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -jar app.jar