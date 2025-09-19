package com.recapme.backend.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class ApiKeyAuthenticationFilter(
    private val validApiKey: String,
    private val isDevelopment: Boolean
) : OncePerRequestFilter() {

    companion object {
        private const val API_KEY_HEADER = "X-API-Key"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // In development mode, ignore API key entirely and always authenticate
        if (isDevelopment) {
            val authentication = UsernamePasswordAuthenticationToken(
                "dev-user",
                null,
                emptyList()
            )
            SecurityContextHolder.getContext().authentication = authentication
            filterChain.doFilter(request, response)
            return
        }

        // Production mode: strict API key validation
        val providedApiKey = request.getHeader(API_KEY_HEADER)

        // Check if this is an API endpoint that requires authentication
        if (request.requestURI.startsWith("/api/")) {
            if (providedApiKey == null) {
                response.status = HttpStatus.UNAUTHORIZED.value()
                response.writer.write("{\"error\":\"Missing X-API-Key header\"}")
                response.contentType = "application/json"
                return
            }

            if (providedApiKey != validApiKey) {
                response.status = HttpStatus.FORBIDDEN.value()
                response.writer.write("{\"error\":\"Invalid API key\"}")
                response.contentType = "application/json"
                return
            }

            // Valid API key - set authentication
            val authentication = UsernamePasswordAuthenticationToken(
                "api-user",
                null,
                emptyList()
            )
            SecurityContextHolder.getContext().authentication = authentication
        }

        filterChain.doFilter(request, response)
    }
}