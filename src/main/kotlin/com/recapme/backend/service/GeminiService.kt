package com.recapme.backend.service

import com.google.genai.Client
import com.google.genai.types.Content
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.Part
import com.google.genai.types.Schema
import com.recapme.backend.model.ConversationRecapResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory

@Service
class GeminiService(
    private val client: Client,
    private val objectMapper: ObjectMapper,
    private val model: String,
    private val conversationRecapSchema: Schema
) {
    private val logger = LoggerFactory.getLogger(GeminiService::class.java)

    fun generateRecap(conversation: String, days: Int, style: String): ConversationRecapResponse {
        logger.info("Starting generateRecap request with days: $days, style: $style")

        return try {
            val systemInstruction = createSystemInstruction(days, style)
            val config = GenerateContentConfig.builder()
                .systemInstruction(systemInstruction)
                .responseMimeType("application/json")
                .responseSchema(conversationRecapSchema)
                .build()

            logger.info("Making request to Gemini API with model: $model using dynamic system instruction")

            val response = client.models.generateContent(
                model,
                conversation,
                config
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

    private fun createSystemInstruction(days: Int, style: String): Content {
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

            IMPORTANT: Only analyze and include messages from the past $days day(s). Ignore any messages that are older than $days day(s) from the most recent message in the conversation.

            Style: $styleInstructions

            Always respond with a JSON object containing:
            - title: A clear, descriptive title for the conversation reflecting the main topics discussed in the specified timeframe
            - participants: Array of all participant names mentioned or speaking within the specified timeframe
            - recap: Summary of what was discussed, decided, or concluded within the past $days day(s), following the specified style. Do not include any system messages.

            Extract participant names directly from the conversation text. Only consider messages within the specified timeframe when creating the summary.
        """.trimIndent()

        return Content.fromParts(Part.fromText(instruction))
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

}