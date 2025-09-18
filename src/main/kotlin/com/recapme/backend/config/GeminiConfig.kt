package com.recapme.backend.config

import com.google.genai.Client
import com.google.genai.types.Content
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.Part
import com.google.genai.types.Schema
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GeminiConfig(
    @Value("\${GEMINI_API_KEY}") private val apiKey: String,
    @Value("\${GEMINI_MODEL}") private val model: String
) {
    @Bean
    fun genAiClient(): Client {
        return Client.builder()
            .apiKey(apiKey)
            .build()
    }

    @Bean
    fun geminiModel(): String {
        return model
    }



    @Bean
    fun conversationRecapSchema(): Schema {
        return Schema.builder()
            .type("object")
            .properties(mapOf(
                "title" to Schema.builder()
                    .type("string")
                    .description("A descriptive title for the conversation")
                    .build(),
                "participants" to Schema.builder()
                    .type("array")
                    .items(Schema.builder()
                        .type("string")
                        .build())
                    .description("Array of participant names")
                    .build(),
                "recap" to Schema.builder()
                    .type("string")
                    .description("Comprehensive summary of the conversation")
                    .build()
            ))
            .required(listOf("title", "participants", "recap"))
            .build()
    }



}