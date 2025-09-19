package com.recapme.backend.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/keep-alive")
class KeepAliveController {

    @GetMapping
    fun keepAlive(): ResponseEntity<String> {
        return ResponseEntity.ok("OK")
    }
}