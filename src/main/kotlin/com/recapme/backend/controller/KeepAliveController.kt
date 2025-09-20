package com.recapme.backend.controller

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/keep-alive")
class KeepAliveController {

    private val logger = LoggerFactory.getLogger(KeepAliveController::class.java)

    @GetMapping
    fun keepAlive(): ResponseEntity<String> {
        val x = System.currentTimeMillis() % 100
        val y = (1..x.toInt()).sum()
        logger.info("Keep-alive check: computed value {}", y)
        return ResponseEntity.ok("OK")
    }
}