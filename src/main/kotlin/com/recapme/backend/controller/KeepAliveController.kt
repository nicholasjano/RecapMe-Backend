package com.recapme.backend.controller

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/api/keep-alive")
class KeepAliveController {

    private val logger = LoggerFactory.getLogger(KeepAliveController::class.java)

    @GetMapping
    fun keepAlive(): ResponseEntity<String> {
        return ResponseEntity.ok("OK")
    }
}