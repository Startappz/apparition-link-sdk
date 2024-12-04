package com.startappz.apparition

import com.startappz.apparition.di.NetworkModule
import com.startappz.apparition.network.ApApi
import com.startappz.apparition.utils.ApLogLevel
import com.startappz.apparition.utils.ApLogger

/**
 * SDK initialization class.
 */
object ApparitionLinkSdk {
    private var apiKey: String? = null

    private val api: ApApi by lazy {
        NetworkModule.api
    }

    /**
     * Initializes the SDK with an API key.
     */
    fun init(apiKey: String) {
        ApparitionLinkSdk.apiKey = apiKey
    }

    fun setLogLevel(level: ApLogLevel) {
        ApLogger.setLogLevel(level)
    }

    suspend fun expand(url: String): Result<String> {
        return api.expand(url)
    }

    internal fun getApiKey(): String {
        return apiKey ?: throw IllegalStateException("SDK not initialized")
    }
}