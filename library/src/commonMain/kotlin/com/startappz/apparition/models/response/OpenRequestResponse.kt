package com.startappz.apparition.models.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class OpenRequestResponse(
    @SerialName("identity_id")
    val identityId: String,
    @SerialName("session_id")
    val sessionId: String,
    @SerialName("has_app")
    val hasApp: Boolean,
    @SerialName("data")
    val dataContent: String,
    @SerialName("browser_fingerprint_id")
    val browserFingerprintId: String
)

@Serializable
data class OpenRequestBody(
    @SerialName("browser_fingerprint_id")
    val fingerprint: String,
)