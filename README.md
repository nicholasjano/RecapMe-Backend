# RecapMe Backend

RecapMe Backend is a robust Spring Boot API service that powers the AI-driven chat summarization capabilities of the RecapMe Android application. Built with Kotlin and leveraging Google's Gemini AI, this backend service transforms WhatsApp conversations into intelligent, contextual summaries with configurable styles and enterprise-grade reliability features.

> **Frontend Repository**: The Android client for this API is available at [RecapMe](https://github.com/nicholasjano/RecapMe)

## Features

### AI-Powered Summarization Engine
- **Google Gemini Integration**: Leverages Gemini AI models for advanced natural language processing
- **Multiple Summary Styles**: Supports concise, detailed, bullet-point, casual, and formal summary formats
- **Participant Recognition**: Automatically identifies and extracts conversation participants
- **Contextual Title Generation**: Creates descriptive titles based on conversation content
- **Intelligent Processing**: Advanced prompt engineering for accurate, contextual summaries

### Enterprise-Ready Infrastructure
- **Rate Limiting**: Redis-based rate limiting (5 requests per 3 hours per IP)
- **Circuit Breaker Pattern**: Resilience4j implementation for fault tolerance
- **Retry Mechanism**: Automatic retry with exponential backoff for transient failures
- **Timeout Management**: Configurable request timeouts to prevent resource exhaustion
- **Graceful Degradation**: Service continues functioning even when optional components fail

### Security & Reliability
- **API Key Authentication**: Secure API access with header-based authentication
- **Input Sanitization**: Comprehensive validation and sanitization to prevent injection attacks
- **CORS Configuration**: Flexible cross-origin resource sharing setup
- **Health Monitoring**: Built-in health check endpoints for service monitoring
- **Prompt Injection Protection**: Advanced pattern detection to prevent AI manipulation

### Developer Experience
- **Docker Support**: Containerized deployment with optimized multi-stage builds
- **Environment Configuration**: Flexible configuration through environment variables
- **Development Mode**: Simplified local development with relaxed security constraints
- **Comprehensive Logging**: Structured logging for debugging and monitoring
- **Auto-documentation**: Self-documenting API responses with clear error messages

## Architecture

RecapMe Backend follows modern microservice architecture patterns with Spring Boot best practices:

### Tech Stack
- **Language**: Kotlin
- **Framework**: Spring Boot 3.5.5
- **AI Service**: Google Gemini AI SDK
- **Caching/Rate Limiting**: Redis (optional)
- **Resilience**: Resilience4j
- **Serialization**: Jackson
- **Build Tool**: Gradle
- **Containerization**: Docker
- **Runtime**: JDK 17

### Project Structure
```
src/main/kotlin/com/recapme/backend/
├── config/
│   ├── DotenvConfig.kt       # Environment variable configuration
│   ├── GeminiConfig.kt       # Gemini AI client setup
│   ├── RedisConfig.kt        # Redis configuration
│   ├── SecurityConfig.kt     # Security and authentication
│   └── WebConfig.kt          # CORS and web configuration
├── controller/
│   ├── HealthController.kt   # Health check endpoints
│   ├── KeepAliveController.kt # Keep-alive endpoint
│   └── RecapController.kt    # Main API endpoint
├── exception/
│   ├── ApiExceptions.kt      # Custom exception definitions
│   └── GlobalExceptionHandler.kt # Global error handling
├── interceptor/
│   └── RateLimitInterceptor.kt # Rate limiting middleware
├── model/
│   ├── GeminiModels.kt       # Gemini response models
│   ├── RecapRequest.kt       # API request model
│   └── RecapResponse.kt      # API response model
├── security/
│   └── ApiKeyAuthenticationFilter.kt # API key validation
├── service/
│   ├── GeminiService.kt      # Gemini AI integration
│   ├── InputSanitizationService.kt # Input validation
│   └── RateLimitService.kt   # Rate limiting logic
└── RecapMeBackendApplication.kt # Application entry point
```

### Key Components
- **GeminiService**: Manages AI model interactions with circuit breaker protection
- **RateLimitService**: Implements Redis-based rate limiting with graceful degradation
- **InputSanitizationService**: Validates and sanitizes user input to prevent attacks
- **GlobalExceptionHandler**: Provides consistent error responses across the API

## Setup & Installation

### Prerequisites
- JDK 17+
- Gradle 8+
- Docker (optional, for containerized deployment)
- Redis (optional, for rate limiting)
- Google Gemini API key

### Environment Configuration

1. **Create environment file**
   ```bash
   cp .env.example .env
   ```

2. **Configure required variables**
   ```env
   # Google Gemini API Configuration (REQUIRED)
   GEMINI_API_KEY=your_gemini_api_key_here
   GEMINI_MODEL=gemini-2.5-flash
   
   # API Security (REQUIRED for production)
   API_KEY=your_secure_api_key_here
   
   # Redis Configuration (OPTIONAL - for rate limiting)
   REDIS_URL=redis://default:password@redis-host:6379
   
   # CORS Configuration
   CORS_ALLOWED_ORIGINS=https://yourdomain.com,https://app.yourdomain.com
   
   # Development Mode
   DEVELOPMENT=false
   ```

### Local Development

1. **Clone the repository**
   ```bash
   git clone https://github.com/nicholasjano/RecapMe-Backend
   cd RecapMe-Backend
   ```

2. **Run with Gradle**
   ```bash
   # Development mode
   export DEVELOPMENT=true
   ./gradlew bootRun
   
   # Production mode
   source .env
   ./gradlew bootRun
   ```

3. **Access the API**
   ```bash
   # Health check
   curl http://localhost:8080/health
   
   # API endpoint (requires X-API-Key header)
   curl -X POST http://localhost:8080/api/recap \
     -H "Content-Type: application/json" \
     -H "X-API-Key: your_api_key_here" \
     -d '{"conversation": "John: Hello!", "style": "concise"}'
   ```

### Docker Deployment

1. **Build Docker image**
   ```bash
   ./build-docker.sh
   ```

2. **Run container with environment variables**
   ```bash
   ./run-docker.sh
   ```

3. **Or manually with docker-compose**
   ```yaml
   version: '3.8'
   services:
     backend:
       image: recapme-backend
       ports:
         - "8080:8080"
       environment:
         - GEMINI_API_KEY=${GEMINI_API_KEY}
         - API_KEY=${API_KEY}
         - REDIS_URL=${REDIS_URL}
         - CORS_ALLOWED_ORIGINS=${CORS_ALLOWED_ORIGINS}
   ```

### Release Build

1. **Build JAR file**
   ```bash
   ./gradlew clean build
   ```

2. **Build Docker image for production**
   ```bash
   docker build -t recapme-backend:latest .
   ```

## API Documentation

### Endpoints

#### Generate Recap
**POST** `/api/recap`

Generates an AI-powered summary of a WhatsApp conversation.

**Headers:**
- `Content-Type: application/json`
- `X-API-Key: {your-api-key}` (required in production)

**Request Body:**
```json
{
  "conversation": "John: Hey, how's the project going?\nMary: Good! Almost done with the API integration.",
  "style": "concise"
}
```

**Supported Styles:**
- `concise`: Brief, to-the-point summary
- `detailed`: Comprehensive overview with context
- `bullet`: Organized bullet-point format
- `casual`: Conversational, friendly tone
- `formal`: Professional, structured summary

**Response:**
```json
{
  "title": "Project Status Discussion",
  "participants": ["John", "Mary"],
  "recap": "John inquired about project progress. Mary confirmed the API integration is nearly complete."
}
```

**Error Responses:**
- `400 Bad Request`: Invalid input or validation failure
- `401 Unauthorized`: Missing API key
- `403 Forbidden`: Invalid API key
- `429 Too Many Requests`: Rate limit exceeded
- `503 Service Unavailable`: External service error

#### Health Check
**GET** `/health`

Returns service health status.

**Response:** `200 OK` with body `"OK"`

#### Keep Alive
**GET** `/keep-alive`

Prevents service from sleeping on platforms with auto-sleep.

**Response:** `200 OK` with body `"OK"`

### Rate Limiting

When Redis is configured, the API enforces rate limits:
- **Limit**: 5 requests per 3 hours per IP address
- **Headers**: Response includes rate limit information:
    - `X-RateLimit-Limit`: Maximum requests allowed
    - `X-RateLimit-Remaining`: Requests remaining in current window
    - `X-RateLimit-Reset`: Unix timestamp when the limit resets
    - `Retry-After`: Seconds until retry (on 429 responses)

## Testing

### Unit Tests
```bash
# Run all tests
./gradlew test

# Run with coverage
./gradlew test jacocoTestReport
```

### Integration Tests
```bash
# Run with Testcontainers (requires Docker)
./gradlew integrationTest
```

### Manual Testing
```bash
# Test health endpoint
curl http://localhost:8080/health

# Test API with sample data
curl -X POST http://localhost:8080/api/recap \
  -H "Content-Type: application/json" \
  -H "X-API-Key: your_api_key" \
  -d @test-data/sample-conversation.json
```

### Test Coverage Areas
- **Service Layer**: AI integration and business logic
- **Security**: API key validation and authentication
- **Rate Limiting**: Redis-based throttling
- **Input Validation**: Sanitization and validation logic
- **Error Handling**: Exception scenarios and fallbacks

## Usage Guide

### Getting a Gemini API Key

1. **Sign up for Google AI Studio**
    - Visit [Google AI Studio](https://makersuite.google.com)
    - Sign in with your Google account
    - Navigate to API Keys section

2. **Generate API Key**
    - Click "Create API Key"
    - Copy the generated key immediately
    - Store securely in your `.env` file

3. **Configure API Limits**
    - Set appropriate quotas in Google Cloud Console
    - Monitor usage to avoid unexpected charges

### Integrating with RecapMe Android App

1. **Deploy the Backend**
    - Deploy to your preferred cloud platform
    - Note the public URL of your deployment

2. **Configure Android App**
    - Update `gradle.properties` in the Android app:
   ```properties
   RECAP_API_KEY=your_api_key_here
   RECAP_API_URL=https://your-backend-url.com/api/
   ```

3. **Test Integration**
    - Build and run the Android app
    - Import a WhatsApp chat export
    - Verify recap generation works correctly

### Customizing Summary Styles

To add new summary styles:

1. **Update GeminiService.kt**
   ```kotlin
   private fun createSystemInstruction(style: String): Content {
       val styleInstructions = when (style.lowercase()) {
           "technical" -> "Provide a technical analysis..."
           // Add your custom style here
       }
   }
   ```

2. **Update InputSanitizationService.kt**
   ```kotlin
   val allowedStyles = setOf("concise", "detailed", "bullet", "casual", "formal", "technical")
   ```

## Deployment

### Render Deployment

The project includes a `render.yaml` configuration for easy deployment:

1. **Connect GitHub repository to Render**
2. **Configure environment variables in Render dashboard**
3. **Deploy automatically on push to main branch**

### Manual Cloud Deployment

The Docker image can be deployed to any container platform:

```bash
# Build and tag image
docker build -t recapme-backend:latest .

# Push to registry
docker tag recapme-backend:latest your-registry/recapme-backend:latest
docker push your-registry/recapme-backend:latest
```

### Production Considerations

#### Security
- Always use strong, unique API keys in production
- Configure CORS origins to match your frontend domains only
- Enable Redis for rate limiting in production
- Use HTTPS/TLS for all production deployments
- Rotate API keys periodically

#### Performance
- Configure JVM heap size based on available memory
- Enable Redis connection pooling for high traffic
- Monitor circuit breaker metrics
- Adjust timeout values based on Gemini API response times

#### Monitoring
- Set up health check monitoring (e.g., UptimeRobot, Pingdom)
- Configure structured logging aggregation (e.g., ELK stack)
- Monitor rate limit violations for potential abuse
- Track Gemini API usage and costs

## Privacy & Data Usage

### Data Processing
- **Transient Processing**: Conversations are processed in-memory and not persisted
- **No Data Storage**: No user conversations are stored on servers
- **Secure Transmission**: All data transmitted over HTTPS in production
- **AI Privacy**: Conversations sent to Gemini API follow Google's privacy policies

### Compliance
- No personal data is logged or stored
- Rate limiting uses IP addresses temporarily (Redis TTL)
- API keys are never logged or exposed
- Full compliance with data protection regulations

### Security Features
- Input validation prevents prompt injection attacks
- Sanitization removes potentially harmful content
- Pattern detection for suspicious input
- Automatic timeout for long-running requests

## Troubleshooting

### Common Issues

**Gemini API Errors**
- Verify API key is valid and has sufficient quota
- Check Gemini model name matches available models
- Review rate limits on Google Cloud Console

**Rate Limiting Not Working**
- Ensure Redis URL is correctly configured
- Check Redis connection and authentication
- Verify Redis server is accessible from application

**CORS Errors**
- Add frontend domain to `CORS_ALLOWED_ORIGINS`
- Ensure origins include protocol (https://)
- Check for trailing slashes in origin URLs

**Authentication Failures**
- Verify `X-API-Key` header is included in requests
- Check API key matches configured value
- In development, set `DEVELOPMENT=true` to bypass auth

### Error Messages

| Error | Cause | Solution |
|-------|-------|----------|
| "Missing X-API-Key header" | No API key provided | Include `X-API-Key` header in request |
| "Invalid API key" | Incorrect API key | Verify API key matches configuration |
| "Rate limit exceeded" | Too many requests | Wait for rate limit window to reset |
| "Service temporarily unavailable" | Gemini API down or circuit open | Retry after backoff period |
| "Invalid style" | Unsupported summary style | Use one of: concise, detailed, bullet, casual, formal |
| "Input contains potentially malicious content" | Suspicious patterns detected | Remove injection attempts from input |

### Logging

Enable debug logging for troubleshooting:

```properties
# In application.properties
logging.level.com.recapme.backend=DEBUG
logging.level.com.google.genai=DEBUG
```

## Build Configuration

### Build Types
- **Development**: Relaxed security, verbose logging
- **Production**: Full security, optimized performance

### Dependencies
Key dependencies include:
- Spring Boot Starter Web
- Spring Boot Starter Security
- Google Gemini AI SDK
- Redis (Spring Data Redis)
- Resilience4j
- Jackson for JSON processing
- Kotlin Coroutines

### Gradle Commands
```bash
# Clean build
./gradlew clean

# Run tests
./gradlew test

# Build JAR
./gradlew build

# Run application
./gradlew bootRun

# Generate dependency report
./gradlew dependencies
```

## License

This project is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file for details.

```
Copyright 2025 Nicholas Jano

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```