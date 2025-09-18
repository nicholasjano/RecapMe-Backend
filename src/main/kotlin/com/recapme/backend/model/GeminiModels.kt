package com.recapme.backend.model

data class GenerateRequest(val prompt: String)
data class GenerateResponse(val text: String)

data class ConversationRecapRequest(val conversation: String)
data class ConversationRecapResponse(
    val title: String,
    val participants: List<String>,
    val recap: String
)