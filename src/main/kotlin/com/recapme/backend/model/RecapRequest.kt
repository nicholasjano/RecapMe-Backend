package com.recapme.backend.model

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class RecapRequest(
    @field:NotBlank(message = "Conversation cannot be blank")
    val conversation: String,

    @field:NotNull(message = "Days cannot be null")
    @field:Min(value = 1, message = "Days must be at least 1")
    val days: Int,

    @field:NotBlank(message = "Style cannot be blank")
    val style: String
)