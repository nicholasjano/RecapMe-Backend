package com.recapme.backend.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {

    @GetMapping("/health")
    fun health(): ResponseEntity<String> {
        return ResponseEntity.ok("OK")
    }

    @RequestMapping(path = ["/"], method = [RequestMethod.GET, RequestMethod.HEAD])
    fun root(): ResponseEntity<String> {
        return ResponseEntity.ok("RecapMe Backend API")
    }
}