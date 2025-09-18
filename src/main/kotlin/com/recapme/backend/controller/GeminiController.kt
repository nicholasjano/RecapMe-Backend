package com.recapme.backend.controller

import com.recapme.backend.model.GenerateRequest
import com.recapme.backend.model.GenerateResponse
import com.recapme.backend.model.ConversationRecapRequest
import com.recapme.backend.model.ConversationRecapResponse
import com.recapme.backend.service.GeminiService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/gemini")
class GeminiController(
    private val geminiService: GeminiService
) {
    @PostMapping("/generate")
    fun generate(@RequestBody req: GenerateRequest): GenerateResponse {
        val text = geminiService.generateText(req.prompt)
        return GenerateResponse(text)
    }

    @PostMapping("/recap")
    fun generateRecap(@RequestBody req: ConversationRecapRequest): ConversationRecapResponse {
        return geminiService.generateConversationRecap(req.conversation)
    }
}