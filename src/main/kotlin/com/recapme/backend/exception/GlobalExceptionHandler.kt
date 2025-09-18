package com.recapme.backend.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(RateLimitExceededException::class)
    fun handleRateLimitExceeded(e: RateLimitExceededException): ResponseEntity<Map<String, Any>> {
        val response: Map<String, Any> = mapOf(
            "error" to "Rate limit exceeded",
            "message" to (e.message ?: "Rate limit exceeded"),
            "retryAfter" to e.retryAfterSeconds
        )

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
            .header("Retry-After", e.retryAfterSeconds.toString())
            .body(response)
    }
}