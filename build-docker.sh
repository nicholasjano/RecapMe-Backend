#!/bin/bash

# Build Docker image (no secrets in build args)
docker build -t recapme-backend .

echo "Build complete!"
echo ""
echo "To run with your .env file:"
echo "source .env && docker run -p 8080:8080 \\"
echo "  -e GEMINI_API_KEY=\"\$GEMINI_API_KEY\" \\"
echo "  -e API_KEY=\"\$API_KEY\" \\"
echo "  -e REDIS_URL=\"\$REDIS_URL\" \\"
echo "  recapme-backend"