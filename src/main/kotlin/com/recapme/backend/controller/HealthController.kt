package com.recapme.backend.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/actuator/health")
class HealthController {

    @GetMapping
    fun health(): ResponseEntity<String> {
        return ResponseEntity.ok("OK")
    }
}