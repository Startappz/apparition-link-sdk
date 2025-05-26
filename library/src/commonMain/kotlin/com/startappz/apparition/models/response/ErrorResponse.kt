package com.startappz.apparition.models.response

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val error: ErrorDetails? = null
)

@Serializable
data class ErrorDetails(
    val message: String? = null
)
