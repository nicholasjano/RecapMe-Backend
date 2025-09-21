package com.recapme.backend.model

import jakarta.validation.constraints.NotBlank

data class RecapRequest(
    @field:NotBlank(message = "Conversation cannot be blank")
    val conversation: String,

    @field:NotBlank(message = "Style cannot be blank")
    val style: String
)