package com.recapme.backend.controller

import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/keep-alive")
class KeepAliveController(
    private val redisTemplate: RedisTemplate<String, String>?
) {

    private val logger = LoggerFactory.getLogger(KeepAliveController::class.java)

    @GetMapping
    fun keepAlive(): ResponseEntity<String> {
        // Ping Redis
        try {
            redisTemplate?.connectionFactory?.connection?.ping()
            logger.info("Redis keep-alive ping successful")
        } catch (e: Exception) {
            logger.warn("Redis keep-alive ping failed: {}", e.message)
        }

        val headers = HttpHeaders()
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate")
        headers.add("Pragma", "no-cache")
        headers.add("Expires", "0")

        return ResponseEntity.ok().headers(headers).body("OK")
    }
}