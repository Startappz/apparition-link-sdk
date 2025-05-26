package com.startappz.apparition.di

import com.startappz.apparition.ApparitionLinkSDK
import com.startappz.apparition.network.ApApi
import com.startappz.apparition.network.ApRequestQueue
import com.startappz.apparition.network.DefaultApApi
import com.startappz.apparition.platform.platformHttpClient
import com.startappz.apparition.utils.ApLogLevel
import com.startappz.apparition.utils.ApLogger
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Network module for initializing and managing network-related components.
 */
internal object NetworkModule {

    /**
     * API implementation.
     */
    val api: ApApi by lazy {
        DefaultApApi(httpClient)
    }

    /**
     * Ktor HTTP client.
     */
    private val httpClient: HttpClient by lazy {
        platformHttpClient().httpClient.config {
            install(ContentNegotiation) {
                json(json)
            }

            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 3)
                exponentialDelay()
            }

            if (ApLogger.apLogLevel != ApLogLevel.OFF) {
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.ALL
                }
            }

            defaultRequest {
                header("X-API-TOKEN", ApparitionLinkSDK.getApiKey())
            }
        }
    }

    private val json: Json by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        Json { ignoreUnknownKeys = true }
    }

    val requestQueue: ApRequestQueue by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        ApRequestQueue(httpClient)
    }
}

