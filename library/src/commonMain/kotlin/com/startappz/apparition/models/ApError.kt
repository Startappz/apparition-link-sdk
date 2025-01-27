package com.startappz.apparition.models

/**
 * Represents errors that can occur during the SDK operations.
 */
sealed class ApError(open val info: String?) : Exception(info) {

    /**
     * Represents a network error.
     */
    data class NetworkError(override val info: String) : ApError(info)

    /**
     * Represents an API error.
     */
    data class ApiError(override val info: String) : ApError(info)

    /**
     * Represents an error when API KEY is missing.
     */
    data class NotInitialized(override val info: String) : ApError(info)
}