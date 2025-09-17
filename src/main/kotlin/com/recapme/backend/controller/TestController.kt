package com.recapme.backend.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {

    @GetMapping("/")
    fun home(): String {
        return "RecapMe Backend is running!"
    }

    @GetMapping("/api/test")
    fun test(): Map<String, String> {
        return mapOf("message" to "API is working", "status" to "success")
    }
}