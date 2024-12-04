package com.startappz.apparition

import com.startappz.apparition.di.NetworkModule
import com.startappz.apparition.models.ApError
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

    /**
     * Sets the log level for the SDK.
     */
    fun setLogLevel(level: ApLogLevel) {
        ApLogger.setLogLevel(level)
    }

    /**
     * Expands a URL by return it's content.
     */
    suspend fun expand(url: String): Result<String> {
        return api.expand(url)
    }

    @Throws(ApError.NotInitialized::class)
    internal fun getApiKey(): String {
        return apiKey
            ?: throw ApError.NotInitialized("SDK in not initialized. Please initialize the SDK with ApparitionLinkSdk.init(api_key)")
    }
}