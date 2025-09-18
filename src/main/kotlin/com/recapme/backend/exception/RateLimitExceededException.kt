package com.recapme.backend.exception

class RateLimitExceededException(
    message: String = "Rate limit exceeded. Maximum 5 requests per 3 hours allowed.",
    val retryAfterSeconds: Long
) : RuntimeException(message)