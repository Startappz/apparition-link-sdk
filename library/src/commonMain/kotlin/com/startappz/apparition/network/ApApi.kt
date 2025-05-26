package com.startappz.apparition.network

import com.startappz.apparition.models.response.OpenRequestResponse

/**
 * Interface defines the contract for interacting with the Apparition API.
 */
internal interface ApApi {

    /**
     * Expands a given URL content.
     */
    @Throws(Exception::class)
    suspend fun expand(url: String): String

    @Throws(Exception::class)
    suspend fun open(fingerprint: String): OpenRequestResponse
}
