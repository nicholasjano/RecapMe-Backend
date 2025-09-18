package com.recapme.backend.service

import com.recapme.backend.exception.RateLimitExceededException
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
@Primary
@ConditionalOnProperty(name = ["spring.data.redis.url"], matchIfMissing = false)
class RateLimitService(
    private val redisTemplate: RedisTemplate<String, String>
) : RateLimitingService {
    companion object {
        private const val MAX_REQUESTS = 5
        private const val WINDOW_DURATION_HOURS = 3L
        private const val KEY_PREFIX = "rate_limit:"
    }

    override fun checkRateLimit(ipAddress: String) {
        try {
            val key = "$KEY_PREFIX$ipAddress"

            // Get current request count
            val currentCountStr = redisTemplate.opsForValue().get(key)
            val currentCount = currentCountStr?.toIntOrNull() ?: 0

            if (currentCount >= MAX_REQUESTS) {
                // Check TTL to determine when the limit will reset
                val ttl = redisTemplate.getExpire(key)
                val retryAfterSeconds = if (ttl > 0) ttl else Duration.ofHours(WINDOW_DURATION_HOURS).seconds

                throw RateLimitExceededException(retryAfterSeconds = retryAfterSeconds)
            }

            // Increment the counter
            val newCount = redisTemplate.opsForValue().increment(key) ?: 1

            // Set expiration if this is the first request in the window
            if (newCount == 1L) {
                redisTemplate.expire(key, Duration.ofHours(WINDOW_DURATION_HOURS))
            }
        } catch (e: RateLimitExceededException) {
            throw e // Re-throw rate limit exceptions
        } catch (e: Exception) {
            // If Redis is unavailable, log and allow the request (fail open)
            println("Redis unavailable for rate limiting: ${e.message}")
        }
    }

    override fun getRemainingRequests(ipAddress: String): Int {
        return try {
            val key = "$KEY_PREFIX$ipAddress"
            val currentCountStr = redisTemplate.opsForValue().get(key)
            val currentCount = currentCountStr?.toIntOrNull() ?: 0
            maxOf(0, MAX_REQUESTS - currentCount)
        } catch (e: Exception) {
            MAX_REQUESTS // If Redis unavailable, return max (fail open)
        }
    }

    override fun getResetTime(ipAddress: String): Long {
        return try {
            val key = "$KEY_PREFIX$ipAddress"
            val ttl = redisTemplate.getExpire(key)
            if (ttl > 0) Instant.now().epochSecond + ttl else 0
        } catch (e: Exception) {
            0 // If Redis unavailable, return 0
        }
    }
}