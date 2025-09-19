package com.recapme.backend.config

import com.recapme.backend.security.ApiKeyAuthenticationFilter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    @Value("\${app.api.key:}") private val apiKey: String,
    @Value("\${app.development:false}") private val isDevelopment: Boolean
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/actuator/health/**").permitAll()
                    .anyRequest().permitAll()
            }
            .addFilterBefore(ApiKeyAuthenticationFilter(apiKey, isDevelopment), UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }
}