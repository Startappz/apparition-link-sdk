package com.startappz.apparition.models

/**
 * Represents errors that can occur during the SDK operations.
 */
sealed class ApError : Exception() {

    /**
     * Represents a network error.
     */
    data class NetworkError(val info: String) : ApError()

    /**
     * Represents an API error.
     */
    data class ApiError(val info: String) : ApError()
}