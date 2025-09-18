package com.recapme.backend.interceptor

import com.recapme.backend.service.RateLimitingService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class RateLimitInterceptor(
    private val rateLimitingService: RateLimitingService
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        // Only apply rate limiting to recap endpoints
        if (request.requestURI.startsWith("/api/recap")) {
            val clientIp = getClientIpAddress(request)

            try {
                rateLimitingService.checkRateLimit(clientIp)

                // Add rate limit headers to response
                val remainingRequests = rateLimitingService.getRemainingRequests(clientIp)
                val resetTime = rateLimitingService.getResetTime(clientIp)

                response.addHeader("X-RateLimit-Limit", "5")
                response.addHeader("X-RateLimit-Remaining", remainingRequests.toString())
                if (resetTime > 0) {
                    response.addHeader("X-RateLimit-Reset", resetTime.toString())
                }

                return true
            } catch (e: Exception) {
                // Rate limit exceeded will be handled by global exception handler
                throw e
            }
        }

        return true
    }

    private fun getClientIpAddress(request: HttpServletRequest): String {
        // Check for X-Forwarded-For header (common in load balancers/proxies)
        val xForwardedFor = request.getHeader("X-Forwarded-For")
        if (!xForwardedFor.isNullOrBlank()) {
            return xForwardedFor.split(",")[0].trim()
        }

        // Check for X-Real-IP header
        val xRealIp = request.getHeader("X-Real-IP")
        if (!xRealIp.isNullOrBlank()) {
            return xRealIp
        }

        // Fallback to remote address
        return request.remoteAddr ?: "unknown"
    }
}