package com.recapme.backend.model

import com.fasterxml.jackson.annotation.JsonProperty

data class RecapResponse(
    @JsonProperty("title")
    val title: String,

    @JsonProperty("participants")
    val participants: List<String>,

    @JsonProperty("recap")
    val recap: String
)