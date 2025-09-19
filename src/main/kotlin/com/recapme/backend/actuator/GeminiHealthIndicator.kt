package com.recapme.backend.actuator

import com.google.genai.Client
import org.springframework.boot.actuator.health.Health
import org.springframework.boot.actuator.health.HealthIndicator
import org.springframework.stereotype.Component

@Component
class GeminiHealthIndicator(
    private val client: Client,
    private val model: String
) : HealthIndicator {

    override fun health(): Health {
        return try {
            // Simple health check - just verify the client is configured properly
            // We don't make an actual API call to avoid unnecessary costs
            val isConfigured = client != null && model.isNotBlank()

            if (isConfigured) {
                Health.up()
                    .withDetail("status", "Gemini client configured")
                    .withDetail("model", model)
                    .build()
            } else {
                Health.down()
                    .withDetail("status", "Gemini client not properly configured")
                    .build()
            }
        } catch (e: Exception) {
            Health.down()
                .withDetail("status", "Gemini client error")
                .withDetail("error", e.message)
                .build()
        }
    }
}