package com.recapme.backend.service

import com.google.genai.Client
import com.google.genai.types.GenerateContentConfig
import com.recapme.backend.model.ConversationRecapResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory

@Service
class GeminiService(
    private val client: Client,
    private val objectMapper: ObjectMapper,
    private val model: String,
    private val recapConfig: GenerateContentConfig
) {
    private val logger = LoggerFactory.getLogger(GeminiService::class.java)

    fun generateRecap(prompt: String): ConversationRecapResponse {
        logger.info("Starting generateRecap request")

        return try {
            logger.info("Making request to Gemini API with model: $model using pre-configured content recap config")

            val response = client.models.generateContent(
                model,
                prompt,
                recapConfig
            )

            val responseText = response.text() ?: ""
            if (responseText.isBlank()) {
                throw RuntimeException("No response text from Gemini API")
            }

            logger.info("Received structured JSON response: $responseText")

            objectMapper.readValue(responseText, ConversationRecapResponse::class.java)
        } catch (e: Exception) {
            logger.error("Failed to generate recap", e)
            throw RuntimeException("Failed to generate recap: ${e.message}", e)
        }
    }

    fun generateText(prompt: String): String {
        logger.info("Starting generateText request")

        return try {
            logger.info("Making request to Gemini API with model: $model")

            val response = client.models.generateContent(
                model,
                prompt,
                GenerateContentConfig.builder().build()
            )

            response.text() ?: ""
        } catch (e: Exception) {
            logger.error("Failed to generate text", e)
            throw RuntimeException("Failed to generate text: ${e.message}", e)
        }
    }

    fun generateConversationRecap(conversation: String): ConversationRecapResponse {
        logger.info("Starting generateConversationRecap request")

        val prompt = "Analyze this conversation and provide a structured summary:\n\n$conversation"

        return try {
            logger.info("Making request to Gemini API with model: $model using pre-configured conversation recap config")

            val response = client.models.generateContent(
                model,
                prompt,
                recapConfig
            )

            val responseText = response.text() ?: ""
            if (responseText.isBlank()) {
                throw RuntimeException("No response text from Gemini API")
            }

            logger.info("Received structured JSON response: $responseText")

            objectMapper.readValue(responseText, ConversationRecapResponse::class.java)
        } catch (e: Exception) {
            logger.error("Failed to generate conversation recap", e)
            throw RuntimeException("Failed to generate conversation recap: ${e.message}", e)
        }
    }
}