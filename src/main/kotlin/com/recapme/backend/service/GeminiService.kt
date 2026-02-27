package com.recapme.backend.service

import com.google.genai.Client
import com.google.genai.errors.ClientException
import com.google.genai.types.Content
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.Part
import com.google.genai.types.Schema
import com.recapme.backend.model.ConversationRecapResponse
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import io.github.resilience4j.timelimiter.annotation.TimeLimiter
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture

@Service
class GeminiService(
    private val client: Client,
    private val objectMapper: ObjectMapper,
    private val model: String,
    private val conversationRecapSchema: Schema
) {
    private val logger = LoggerFactory.getLogger(GeminiService::class.java)

    @CircuitBreaker(name = "gemini", fallbackMethod = "generateRecapFallback")
    @Retry(name = "gemini")
    @TimeLimiter(name = "gemini")
    fun generateRecap(conversation: String, style: String): CompletableFuture<ConversationRecapResponse> {
        return CompletableFuture.supplyAsync {
            try {
                val systemInstruction = createSystemInstruction(style)
                val config = GenerateContentConfig.builder()
                    .systemInstruction(systemInstruction)
                    .responseMimeType("application/json")
                    .responseSchema(conversationRecapSchema)
                    .build()

                val response = client.models.generateContent(
                    model,
                    conversation,
                    config
                )

                val responseText = response.text() ?: ""
                if (responseText.isBlank()) {
                    throw RuntimeException("No response text from Gemini API")
                }

                objectMapper.readValue(responseText, ConversationRecapResponse::class.java)
            } catch (e: ClientException) {
                logger.error("Failed to generate recap: API error code ${e.code()}", e)
                throw e
            } catch (e: Exception) {
                logger.error("Failed to generate recap", e)
                throw RuntimeException("Failed to generate recap: ${e.message}", e)
            }
        }
    }

    private fun createSystemInstruction(style: String): Content {
        val styleInstructions = when (style.lowercase()) {
            "concise" -> "Provide a brief, to-the-point summary focusing only on the most important points."
            "detailed" -> "Provide a comprehensive, thorough summary including context, key discussions, and detailed outcomes."
            "bullet" -> "Provide the summary in bullet point format with clear, organized sections."
            "casual" -> "Provide a relaxed, informal summary as if explaining to a friend."
            "formal" -> "Provide a professional, structured summary suitable for business or academic contexts."
            else -> "Provide a balanced summary with good detail while remaining clear and readable."
        }

        val instruction = """
            You are a conversation analyst. Your role is to analyze conversations and extract structured information.

            Style: $styleInstructions

            Always respond with a JSON object containing:
            - title: A clear, descriptive title for the conversation reflecting the main topics discussed
            - participants: Array of all participant names mentioned or speaking in the conversation
            - recap: Summary of what was discussed, decided, or concluded, following the specified style.

            Extract participant names only from the sender labels that appear before the colon (:) in message formats like "Name: message text" – do not include names mentioned inside the message body – count each sender name only once, even if nicknames or variations appear elsewhere.
            
            Important: Ignore system messages (e.g., "You created this group", "Messages and calls are end-to-end encrypted", timestamps, or other metadata) that may appear in the conversation, usually at the top.
        """.trimIndent()

        return Content.fromParts(Part.fromText(instruction))
    }

    // Fallback method for circuit breaker
    fun generateRecapFallback(conversation: String, style: String, ex: Exception): CompletableFuture<ConversationRecapResponse> {
        logger.warn("Gemini API is unavailable, using fallback response. Reason: ${ex.message}")

        val fallbackResponse = ConversationRecapResponse(
            title = "Service Temporarily Unavailable",
            participants = listOf("System"),
            recap = "We're sorry, but our AI recap service is temporarily unavailable. " +
                    "This could be due to high demand or maintenance. Please try again in a few minutes. " +
                    "Your conversation data has been safely processed and no information was lost."
        )

        return CompletableFuture.completedFuture(fallbackResponse)
    }

    fun generateText(prompt: String): String {
        return try {
            val response = client.models.generateContent(
                model,
                prompt,
                GenerateContentConfig.builder().build()
            )

            response.text() ?: ""
        } catch (e: ClientException) {
            logger.error("Failed to generate text: API error code ${e.code()}", e)
            throw e
        } catch (e: Exception) {
            logger.error("Failed to generate text", e)
            throw RuntimeException("Failed to generate text: ${e.message}", e)
        }
    }

}