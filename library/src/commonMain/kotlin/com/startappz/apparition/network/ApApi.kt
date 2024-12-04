package com.startappz.apparition.network

/**
 * Interface defines the contract for interacting with the Apparition API.
 */
internal interface ApApi {

    /**
     * Expands a given URL content.
     */
    suspend fun expand(url: String): Result<String>
}
