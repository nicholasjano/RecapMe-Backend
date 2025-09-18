package com.recapme.backend.config

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource
import javax.annotation.PostConstruct

@Configuration
class DotenvConfig(private val environment: ConfigurableEnvironment) {

    @PostConstruct
    fun loadDotenv() {
        val dotenv = Dotenv.configure()
            .directory(".")
            .ignoreIfMissing()
            .load()

        val dotenvProperties = mutableMapOf<String, Any>()

        // Load all .env variables into Spring environment
        dotenv.entries().forEach { entry ->
            dotenvProperties[entry.key] = entry.value
        }

        environment.propertySources.addFirst(MapPropertySource("dotenv", dotenvProperties))
    }
}