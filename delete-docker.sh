#!/bin/bash

echo "Cleaning up Docker resources for RecapMe Backend..."

# Stop and remove any running containers
echo "Stopping containers..."
docker stop recapme-backend 2>/dev/null || true

echo "Removing containers..."
docker rm recapme-backend 2>/dev/null || true

# Remove the image
echo "Removing image..."
docker rmi recapme-backend 2>/dev/null || true

# Clean up dangling images and build cache
echo "Cleaning up build cache..."
docker image prune -f 2>/dev/null || true

echo "Docker cleanup complete!"
echo ""
echo "You can now run: ./build-docker.sh"