package com.startappz.apparition.network

import com.startappz.apparition.ApparitionLinkSDK
import com.startappz.apparition.di.NetworkModule
import com.startappz.apparition.models.SdkState
import com.startappz.apparition.utils.ApLogger
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class ApRequestQueue(
    private val httpClient: HttpClient,
    private val retryPolicy: RetryPolicy = defaultRetryPolicy,
) {
    private val queue = mutableListOf<NetworkRequest>()
    private val mutex = Mutex()
    private val isSdkInitialized: Boolean
        get() = ApparitionLinkSDK.sdkState == SdkState.Initialised

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun addRequest(request: NetworkRequest) {
        coroutineScope.launch {
            mutex.withLock {
                queue.add(request)
                if (isSdkInitialized) {
                    processQueue()
                }
            }
        }
    }

    fun addRequestAtFront(request: NetworkRequest) {
        coroutineScope.launch {
            mutex.withLock {
                queue.add(0, request)
                if (isSdkInitialized) {
                    processQueue()
                }
            }
        }
    }

    private suspend fun processQueue() {
        while (queue.isNotEmpty()) {
            val request = mutex.withLock { queue.removeFirstOrNull() } ?: continue
            executeRequest(request)
        }
    }

    private suspend fun executeRequest(request: NetworkRequest) {
        var attempt = 0
        while (attempt < retryPolicy.maxAttempts) {
            try {
                attempt++
                executeRequestInternal(request)
                break // Exit retry loop on success
            } catch (e: Exception) {
                ApLogger.e("Attempt $attempt failed for request to ${request.url}: ${e.message}")
                if (attempt == retryPolicy.maxAttempts) {
                    ApLogger.e("Max retry attempts reached for request to ${request.url}")
                    break
                }
                delay(retryPolicy.retryDelayMillis) // Wait before retrying
            }
        }
    }

    private suspend fun executeRequestInternal(request: NetworkRequest) {
        try {
            when (request.method) {
                HttpMethod.Get -> {
                    httpClient.get(request.url) {
                        headers {
                            request.headers.forEach {
                                append(it.key, it.value)
                            }
                        }
                    }
                }

                HttpMethod.Post -> {
                    httpClient.post(request.url) {
                        headers {
                            request.headers.forEach {
                                append(it.key, it.value)
                            }
                        }
                        setBody(request.body)
                    }
                }

                else -> throw IllegalArgumentException("Unsupported HTTP method")
            }
        } catch (e: Exception) {
            ApLogger.e("Request failed: ${e.message}")
            throw e
        } finally {
            httpClient.close()
        }
    }

    companion object {
        fun getInstance(): ApRequestQueue {
            return NetworkModule.requestQueue
        }

        private val defaultRetryPolicy = RetryPolicy(maxAttempts = 5, retryDelayMillis = 3000)
    }
}

internal data class NetworkRequest(
    val url: String,
    val method: HttpMethod,
    val headers: Map<String, String> = emptyMap(),
    val body: Any? = null
)

internal data class RetryPolicy(
    val maxAttempts: Int,
    val retryDelayMillis: Long
)