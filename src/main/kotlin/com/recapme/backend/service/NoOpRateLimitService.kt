package com.recapme.backend.service

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
@ConditionalOnMissingBean(RateLimitService::class)
class NoOpRateLimitService : RateLimitingService {

    override fun checkRateLimit(ipAddress: String) {
        // No-op: Allow all requests when Redis is not configured
        // This ensures the app works without Redis but without rate limiting
    }

    override fun getRemainingRequests(ipAddress: String): Int {
        return 5 // Always return max requests available
    }

    override fun getResetTime(ipAddress: String): Long {
        return 0 // No reset time needed
    }
}