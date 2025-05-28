package com.startappz.apparition.network

import com.startappz.apparition.models.ApError
import com.startappz.apparition.platform.isDebug
import com.startappz.apparition.utils.ApLogger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse

/**
 * Default implementation of the ApApi interface.
 */
internal class DefaultApApi(
    private val httpClient: HttpClient
) : ApApi {

    private val baseUrl = if (isDebug) BASE_URL_STAGING else BASE_URL

    /**
     * Concrete implementation of the expand method.
     */
    override suspend fun expand(url: String): String {
        ApLogger.d(message = "Expanding: $url")

        return try {
            val httpResponse: HttpResponse = httpClient.get("$baseUrl/links/expand") {
                parameter("url", url)
            }

            if (httpResponse.isSuccess()) {
                val body: String = httpResponse.body()
                ApLogger.d(message = "Response body: $body")
                body
            } else {
                throw ApError.ApiError("Request failed with status: ${httpResponse.status.value}")
            }
        } catch (exception: Exception) {
            ApLogger.e(message = "Request failed with exception: ${exception.message}")
            throw ApError.NetworkError("Request failed with exception: ${exception.message}")
        }
    }

    companion object {
        private const val BASE_URL = "https://apparition.link/api/v1"
        private const val BASE_URL_STAGING = "https://stg.apparition.link/api/v1"
    }
}

private fun HttpResponse.isSuccess(): Boolean {
    return status.value in 200..299
}