package com.recapme.backend.actuator

import org.springframework.boot.actuator.health.Health
import org.springframework.boot.actuator.health.HealthIndicator
import org.springframework.stereotype.Component
import java.lang.management.ManagementFactory

@Component
class CustomHealthIndicator : HealthIndicator {

    override fun health(): Health {
        return try {
            val runtime = Runtime.getRuntime()
            val memoryBean = ManagementFactory.getMemoryMXBean()
            val heapMemory = memoryBean.heapMemoryUsage

            val freeMemory = runtime.freeMemory()
            val totalMemory = runtime.totalMemory()
            val maxMemory = runtime.maxMemory()
            val usedMemory = totalMemory - freeMemory
            val memoryUsagePercent = (usedMemory.toDouble() / maxMemory.toDouble()) * 100

            // Consider the application unhealthy if memory usage exceeds 90%
            if (memoryUsagePercent > 90) {
                Health.down()
                    .withDetail("memoryUsage", "${memoryUsagePercent.toInt()}%")
                    .withDetail("reason", "High memory usage")
                    .build()
            } else {
                Health.up()
                    .withDetail("memoryUsage", "${memoryUsagePercent.toInt()}%")
                    .withDetail("availableProcessors", runtime.availableProcessors())
                    .withDetail("heapUsed", "${heapMemory.used / (1024 * 1024)} MB")
                    .withDetail("heapMax", "${heapMemory.max / (1024 * 1024)} MB")
                    .build()
            }
        } catch (e: Exception) {
            Health.down()
                .withDetail("error", e.message)
                .build()
        }
    }
}