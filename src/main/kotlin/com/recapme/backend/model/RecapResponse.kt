package com.recapme.backend.model

import com.fasterxml.jackson.annotation.JsonProperty

data class RecapResponse(
    @JsonProperty("Title")
    val title: String,

    @JsonProperty("Users")
    val users: List<String>,

    @JsonProperty("Recap")
    val recap: String
)