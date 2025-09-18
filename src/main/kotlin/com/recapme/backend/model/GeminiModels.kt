package com.recapme.backend.model

data class ConversationRecapResponse(
    val title: String,
    val participants: List<String>,
    val recap: String
)