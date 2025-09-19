package com.recapme.backend.controller

import com.recapme.backend.exception.ExternalServiceException
import com.recapme.backend.model.RecapRequest
import com.recapme.backend.model.RecapResponse
import com.recapme.backend.service.GeminiService
import com.recapme.backend.service.InputSanitizationService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import jakarta.validation.Valid

@RestController
@RequestMapping("/api/recap")
class RecapController(
    private val geminiService: GeminiService,
    private val inputSanitizationService: InputSanitizationService
) {

    private val logger = LoggerFactory.getLogger(RecapController::class.java)

    @PostMapping
    suspend fun generateRecap(@Valid @RequestBody request: RecapRequest): ResponseEntity<RecapResponse> {
        logger.info("Received recap request for {} days with style: {}", request.days, request.style)

        // Sanitize and validate inputs
        val sanitizedConversation = inputSanitizationService.sanitizeConversationInput(request.conversation)
        val validatedDays = inputSanitizationService.validateDays(request.days)
        val validatedStyle = inputSanitizationService.validateStyle(request.style)

        logger.debug("Input validation completed successfully")

        return try {
            val geminiResponse = geminiService.generateRecap(
                conversation = sanitizedConversation,
                days = validatedDays,
                style = validatedStyle
            )
            val response = RecapResponse(
                title = geminiResponse.title,
                users = geminiResponse.participants,
                recap = geminiResponse.recap
            )
            logger.info("Successfully generated recap with title: {}", geminiResponse.title)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            logger.error("Failed to generate recap: {}", e.message, e)
            throw ExternalServiceException("Failed to generate recap", e)
        }
    }
}