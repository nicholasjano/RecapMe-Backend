package com.recapme.backend.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(RateLimitExceededException::class)
    fun handleRateLimitExceeded(e: RateLimitExceededException): ResponseEntity<Map<String, Any>> {
        logger.warn("Rate limit exceeded: {}", e.message)
        val response: Map<String, Any> = mapOf(
            "error" to "Rate limit exceeded",
            "message" to "Too many requests. Please try again later.",
            "retry_after" to e.retryAfterSeconds
        )

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
            .header("Retry-After", e.retryAfterSeconds.toString())
            .body(response)
    }

    @ExceptionHandler(InvalidInputException::class)
    fun handleInvalidInput(e: InvalidInputException): ResponseEntity<Map<String, String>> {
        logger.warn("Invalid input: {}", e.message)
        val response = mapOf(
            "error" to "Invalid input",
            "message" to "The provided input is invalid. Please check your request and try again."
        )
        return ResponseEntity.badRequest().body(response)
    }

    @ExceptionHandler(ValidationException::class)
    fun handleValidation(e: ValidationException): ResponseEntity<Map<String, String>> {
        logger.warn("Validation error: {}", e.message)
        val response = mapOf(
            "error" to "Validation error",
            "message" to "Request validation failed. Please check your input."
        )
        return ResponseEntity.badRequest().body(response)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(e: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
        logger.warn("Method argument validation failed: {}", e.message)
        val errors = e.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "Invalid value")
        }
        val response = mapOf(
            "error" to "Validation failed",
            "message" to "Request validation failed",
            "details" to errors
        )
        return ResponseEntity.badRequest().body(response)
    }

    @ExceptionHandler(ExternalServiceException::class)
    fun handleExternalService(e: ExternalServiceException): ResponseEntity<Map<String, String>> {
        logger.error("External service error: {}", e.message, e)
        val response = mapOf(
            "error" to "Service unavailable",
            "message" to "An external service is currently unavailable. Please try again later."
        )
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response)
    }

    @ExceptionHandler(ServiceUnavailableException::class)
    fun handleServiceUnavailable(e: ServiceUnavailableException): ResponseEntity<Map<String, String>> {
        logger.error("Service unavailable: {}", e.message, e)
        val response = mapOf(
            "error" to "Service unavailable",
            "message" to "The service is temporarily unavailable. Please try again later."
        )
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response)
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthentication(e: AuthenticationException): ResponseEntity<Map<String, String>> {
        logger.warn("Authentication error: {}", e.message)
        val response = mapOf(
            "error" to "Authentication failed",
            "message" to "Invalid API key or authentication credentials"
        )
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(e: AccessDeniedException): ResponseEntity<Map<String, String>> {
        logger.warn("Access denied: {}", e.message)
        val response = mapOf(
            "error" to "Access denied",
            "message" to "You don't have permission to access this resource"
        )
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneral(e: Exception): ResponseEntity<Map<String, String>> {
        logger.error("Unexpected error: {}", e.message, e)
        val response = mapOf(
            "error" to "Internal server error",
            "message" to "An unexpected error occurred. Please try again later."
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }
}