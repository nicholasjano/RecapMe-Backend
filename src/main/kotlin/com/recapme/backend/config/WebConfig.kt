package com.recapme.backend.config

import com.recapme.backend.interceptor.RateLimitInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val rateLimitInterceptor: RateLimitInterceptor,
    @Value("\${app.cors.allowed-origins}") private val allowedOrigins: String,
    @Value("\${app.development:false}") private val isDevelopment: Boolean
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(rateLimitInterceptor)
            .addPathPatterns("/api/recap/**")
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        val configuredOrigins = allowedOrigins.split(",").map { it.trim() }.filter { it.isNotBlank() }

        val origins = if (isDevelopment) {
            // In development, add localhost origins to the configured origins
            val developmentOrigins = listOf("http://localhost:3000", "http://localhost:8080", "http://localhost:5173")
            (configuredOrigins + developmentOrigins).distinct().toTypedArray()
        } else {
            // In production, only use configured origins - if empty, block all
            if (configuredOrigins.isEmpty()) {
                arrayOf("https://nonexistent-domain.invalid") // Block all origins
            } else {
                configuredOrigins.toTypedArray()
            }
        }

        registry.addMapping("/api/**")
            .allowedOrigins(*origins)
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("Content-Type", "Authorization", "X-API-Key")
            .allowCredentials(false)
            .maxAge(3600)
    }
}