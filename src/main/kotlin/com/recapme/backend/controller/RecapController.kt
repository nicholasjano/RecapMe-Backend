package com.recapme.backend.controller

import com.recapme.backend.model.RecapRequest
import com.recapme.backend.model.RecapResponse
import com.recapme.backend.service.GeminiService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import jakarta.validation.Valid

@RestController
@RequestMapping("/api/recap")
class RecapController(
    private val geminiService: GeminiService
) {

    @PostMapping
    suspend fun generateRecap(@Valid @RequestBody request: RecapRequest): ResponseEntity<RecapResponse> {
        return try {
            val geminiResponse = geminiService.generateRecap(
                conversation = request.conversation,
                days = request.days,
                style = request.style
            )
            val response = RecapResponse(
                title = geminiResponse.title,
                users = geminiResponse.participants,
                recap = geminiResponse.recap
            )
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }
}