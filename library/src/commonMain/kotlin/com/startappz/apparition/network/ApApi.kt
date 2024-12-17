package com.startappz.apparition.network

/**
 * Interface defines the contract for interacting with the Apparition API.
 */
internal interface ApApi {

    /**
     * Expands a given URL content.
     */
    @Throws(Exception::class)
    suspend fun expand(url: String): String
}
